package com.mapex.data.repository

import com.mapex.data.remote.RetrofitClient
import com.mapex.data.remote.dto.CountryDTO
import com.mapex.domain.model.Country
import com.mapex.domain.repository.CountryRepository

object CountryRepositoryImpl : CountryRepository {

    override suspend fun getAllCountries(): Result<List<Country>> = try {
        val countryDTOs = RetrofitClient.apiService.getAllCountries()
        Result.success(countryDTOs.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchCountriesByName(name: String): Result<List<Country>> = try {
        val countryDTOs = RetrofitClient.apiService.searchCountriesByName(name)
        Result.success(countryDTOs.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountriesByRegion(region: String): Result<List<Country>> = try {
        val countryDTOs = RetrofitClient.apiService.getCountriesByRegion(region)
        Result.success(countryDTOs.map { it.toDomain() })
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getCountryByCode(code: String): Result<Country?> = try {
        val countryDTOs = RetrofitClient.apiService.getCountryByCode(code)
        Result.success(countryDTOs.firstOrNull()?.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun CountryDTO.toDomain(): Country {
        val resolvedCodeAlpha2 = codeAlpha2 ?: ""
        val resolvedCodeAlpha3 = codeAlpha3 ?: resolvedCodeAlpha2.ifBlank { name.common }

        // Build international dialing code from root + first suffix
        val callingCode = idd?.root?.let { root ->
            val suffix = idd.suffixes?.firstOrNull() ?: ""
            "$root$suffix"
        }

        // Extract the most recent gini value (highest year key)
        val latestGini = gini?.entries?.maxByOrNull { it.key }?.value

        return Country(
            id = resolvedCodeAlpha3,
            commonName = name.common,
            officialName = name.official,
            region = region ?: "N/A",
            subregion = subregion ?: "N/A",
            capital = capital ?: emptyList(),
            population = population ?: 0,
            area = area,
            timezones = timezones ?: emptyList(),
            languages = languages?.values?.toList() ?: emptyList(),
            currencies = currencies?.values?.map { "${it.name}${it.symbol?.let { s -> " ($s)" } ?: ""}" }
                ?: emptyList(),
            flags = flags?.png ?: flags?.svg,
            codeAlpha2 = resolvedCodeAlpha2,
            codeAlpha3 = resolvedCodeAlpha3,
            coatOfArms = coatOfArms?.svg ?: coatOfArms?.png,
            borders = borders ?: emptyList(),
            tld = tld ?: emptyList(),
            callingCode = callingCode,
            googleMapsUrl = maps?.googleMaps,
            latlng = latlng ?: emptyList(),
            landlocked = landlocked ?: false,
            gini = latestGini,
            carSide = car?.side,
            startOfWeek = startOfWeek,
            continents = continents ?: emptyList()
        )
    }
}
