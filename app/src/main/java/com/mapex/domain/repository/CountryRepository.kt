package com.mapex.domain.repository

import com.mapex.domain.model.Country

interface CountryRepository {
    suspend fun getAllCountries(): Result<List<Country>>
    suspend fun searchCountriesByName(name: String): Result<List<Country>>
    suspend fun getCountriesByRegion(region: String): Result<List<Country>>
    suspend fun getCountryByCode(code: String): Result<Country?>
}
