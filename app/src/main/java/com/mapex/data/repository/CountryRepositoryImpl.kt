package com.mapex.data.repository

import com.mapex.data.local.dao.CountryDao
import com.mapex.data.remote.RetrofitClient
import com.mapex.data.remote.dto.CountryDTO
import com.mapex.domain.model.Country
import com.mapex.domain.repository.CountryRepository
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope

class CountryRepositoryImpl(
    private val countryDao: CountryDao
) : CountryRepository {

    override fun getPagedCountries(searchQuery: String): Flow<PagingData<Country>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40
            ),
            pagingSourceFactory = {
                if (searchQuery.isBlank()) {
                    countryDao.getPagedCountries()
                } else {
                    countryDao.searchPagedCountries("%$searchQuery%")
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getAllCountries(): Result<List<Country>> {
        return try {
            val mergedCountries = supervisorScope {
                // Estrategia de tres llamadas en paralelo para bypass del límite de 10 campos
                val fields1 = "name,cca2,cca3,region,subregion,capital,population,flags,continents,area"
                val fields2 = "cca3,languages,currencies,timezones,borders,coatOfArms,latlng,maps,idd,car"
                val fields3 = "cca3,landlocked,startOfWeek,gini,tld"
                
                val d1 = async { RetrofitClient.apiService.getAllCountries(fields1) }
                val d2 = async { RetrofitClient.apiService.getAllCountries(fields2) }
                val d3 = async { RetrofitClient.apiService.getAllCountries(fields3) }

                val res1 = d1.await()
                val res2 = d2.await()
                val res3 = d3.await()

                // Combinar los DTOs por su código cca3
                res1.map { country1 ->
                    val country2 = res2.find { it.codeAlpha3 == country1.codeAlpha3 }
                    val country3 = res3.find { it.codeAlpha3 == country1.codeAlpha3 }
                    
                    country1.toDomain(country2, country3)
                }.sortedBy { it.commonName }
            }
            
            countryDao.insertAllWithDetails(
                countries = mergedCountries.map { it.toEntity() },
                details = mergedCountries.map { it.toDetailEntity() }
            )
            
            Result.success(mergedCountries)
        } catch (e: Exception) {
            try {
                // Offline: Cargamos la lista completa incluyendo sus detalles
                val localWithDetails = countryDao.getAllCountriesWithDetail()
                if (localWithDetails.isNotEmpty()) {
                    Result.success(localWithDetails.map { it.country.toDomain(it.detail) })
                } else {
                    Result.failure(e)
                }
            } catch (localEx: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun searchCountriesByName(name: String): Result<List<Country>> = try {
        val countryDTOs = RetrofitClient.apiService.searchCountriesByName(name)
        val domainCountries = countryDTOs.map { it.toDomain() }.sortedBy { it.commonName }
        countryDao.insertAllWithDetails(
            countries = domainCountries.map { it.toEntity() },
            details = domainCountries.map { it.toDetailEntity() }
        )
        Result.success(domainCountries)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountriesByRegion(region: String): Result<List<Country>> = try {
        val countryDTOs = RetrofitClient.apiService.getCountriesByRegion(region)
        val domainCountries = countryDTOs.map { it.toDomain() }.sortedBy { it.commonName }
        countryDao.insertAllWithDetails(
            countries = domainCountries.map { it.toEntity() },
            details = domainCountries.map { it.toDetailEntity() }
        )
        Result.success(domainCountries)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountryByCode(code: String): Result<Country?> {
        return try {
            val countryDTOs = RetrofitClient.apiService.getCountryByCode(code)
            val domainCountry = countryDTOs.firstOrNull()?.toDomain()
            domainCountry?.let {
                countryDao.insertCountries(listOf(it.toEntity()))
                countryDao.insertCountryDetail(it.toDetailEntity())
            }
            Result.success(domainCountry)
        } catch (e: Exception) {
            e.printStackTrace() // Log the error to see details
            try {
                // 1. Intentamos búsqueda exacta por código (ej: "COL")
                var localWithDetail = countryDao.getCountryDetail(code.uppercase())
                
                // 2. Si no lo encuentra, intentamos búsqueda por nombre en todo el caché
                if (localWithDetail == null) {
                    val allLocalWithDetail = countryDao.getAllCountriesWithDetail()
                    localWithDetail = allLocalWithDetail.find { 
                        it.country.id_contries.equals(code, ignoreCase = true) || 
                        it.country.nombre_comun.equals(code, ignoreCase = true) 
                    }
                }

                if (localWithDetail != null) {
                    Result.success(localWithDetail.country.toDomain(localWithDetail.detail))
                } else {
                    Result.failure(e)
                }
            } catch (localEx: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun CountryDTO.toDomain(extra1: CountryDTO? = null, extra2: CountryDTO? = null): Country {
        val resolvedCodeAlpha2 = codeAlpha2 ?: extra1?.codeAlpha2 ?: extra2?.codeAlpha2 ?: ""
        val resolvedCodeAlpha3 = codeAlpha3 ?: extra1?.codeAlpha3 ?: extra2?.codeAlpha3 ?: resolvedCodeAlpha2.ifBlank { name.common }

        val sourceIdd = idd ?: extra1?.idd ?: extra2?.idd
        val callingCode = sourceIdd?.root?.let { root ->
            val suffix = sourceIdd.suffixes?.firstOrNull() ?: ""
            "$root$suffix"
        }

        val sourceGini = gini ?: extra1?.gini ?: extra2?.gini
        val latestGini = sourceGini?.entries?.maxByOrNull { it.key }?.value

        val sourceCar = car ?: extra1?.car ?: extra2?.car

        return Country(
            id = resolvedCodeAlpha3,
            commonName = name.common,
            officialName = name.official,
            region = region ?: extra1?.region ?: extra2?.region ?: "N/A",
            subregion = subregion ?: extra1?.subregion ?: extra2?.subregion ?: "N/A",
            capital = capital ?: extra1?.capital ?: extra2?.capital ?: emptyList(),
            population = population ?: extra1?.population ?: extra2?.population ?: 0,
            area = area ?: extra1?.area ?: extra2?.area,
            timezones = timezones ?: extra1?.timezones ?: extra2?.timezones ?: emptyList(),
            languages = (languages ?: extra1?.languages ?: extra2?.languages)?.values?.toList() ?: emptyList(),
            currencies = (currencies ?: extra1?.currencies ?: extra2?.currencies)?.values?.map { "${it.name}${it.symbol?.let { s -> " ($s)" } ?: ""}" }
                ?: emptyList(),
            flags = flags?.png ?: flags?.svg ?: extra1?.flags?.png ?: extra1?.flags?.svg ?: extra2?.flags?.png ?: extra2?.flags?.svg,
            codeAlpha2 = resolvedCodeAlpha2,
            codeAlpha3 = resolvedCodeAlpha3,
            coatOfArms = coatOfArms?.svg ?: coatOfArms?.png ?: extra1?.coatOfArms?.svg ?: extra1?.coatOfArms?.png ?: extra2?.coatOfArms?.svg ?: extra2?.coatOfArms?.png,
            borders = borders ?: extra1?.borders ?: extra2?.borders ?: emptyList(),
            tld = tld ?: extra1?.tld ?: extra2?.tld ?: emptyList(),
            callingCode = callingCode,
            googleMapsUrl = (maps ?: extra1?.maps ?: extra2?.maps)?.googleMaps,
            latlng = latlng ?: extra1?.latlng ?: extra2?.latlng ?: emptyList(),
            landlocked = landlocked ?: extra1?.landlocked ?: extra2?.landlocked ?: false,
            gini = latestGini,
            carSide = sourceCar?.side,
            startOfWeek = startOfWeek ?: extra1?.startOfWeek ?: extra2?.startOfWeek,
            continents = continents ?: extra1?.continents ?: extra2?.continents ?: emptyList()
        )
    }
}
