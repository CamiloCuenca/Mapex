package com.mapex.data.remote

import com.mapex.data.remote.dto.CountryDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    
    @GET("v3.1/all?fields=cca2,cca3,name,region,capital,flags")
    suspend fun getAllCountries(): List<CountryDTO>
    
    @GET("v3.1/name/{name}")
    suspend fun searchCountriesByName(@Path("name") name: String): List<CountryDTO>
    
    @GET("v3.1/region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<CountryDTO>
    
    @GET("v3.1/alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): List<CountryDTO>
}
