package com.mapex.data.remote

import com.mapex.data.remote.dto.CountryDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("v3.1/all")
    suspend fun getAllCountries(@Query("fields") fields: String): List<CountryDTO>

    @GET("v3.1/name/{name}")
    suspend fun searchCountriesByName(@Path("name") name: String): List<CountryDTO>

    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<CountryDTO>

    // No fields filter – fetch full detail for the country detail screen
    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): List<CountryDTO>
}
