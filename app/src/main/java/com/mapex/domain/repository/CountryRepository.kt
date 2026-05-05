package com.mapex.domain.repository

import androidx.paging.PagingData
import com.mapex.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    suspend fun getAllCountries(): Result<List<Country>>
    fun getPagedCountries(searchQuery: String = ""): Flow<PagingData<Country>>
    suspend fun searchCountriesByName(name: String): Result<List<Country>>
    suspend fun getCountriesByRegion(region: String): Result<List<Country>>
    suspend fun getCountryByCode(code: String): Result<Country?>
}
