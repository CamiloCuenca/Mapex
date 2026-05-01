package com.mapex.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CountryDTO(
    @SerializedName("cca2")
    val codeAlpha2: String?,
    @SerializedName("cca3")
    val codeAlpha3: String?,
    val name: NameDTO,
    val region: String?,
    val subregion: String?,
    val capital: List<String>?,
    val population: Int?,
    val area: Double?,
    val timezones: List<String>?,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyDTO>?,
    val flags: FlagsDTO?
)

data class NameDTO(
    val common: String,
    val official: String
)

data class CurrencyDTO(
    val name: String,
    val symbol: String?
)

data class FlagsDTO(
    val png: String?,
    val svg: String?,
    val alt: String?
)
