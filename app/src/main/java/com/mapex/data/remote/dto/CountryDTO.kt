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
    val flags: FlagsDTO?,
    // Extended fields
    val coatOfArms: CoatOfArmsDTO?,
    val borders: List<String>?,
    val tld: List<String>?,
    val idd: IddDTO?,
    val maps: MapsDTO?,
    val latlng: List<Double>?,
    val landlocked: Boolean?,
    val gini: Map<String, Double>?,
    val car: CarDTO?,
    val startOfWeek: String?,
    val continents: List<String>?
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

data class CoatOfArmsDTO(
    val png: String?,
    val svg: String?
)

data class IddDTO(
    val root: String?,
    val suffixes: List<String>?
)

data class MapsDTO(
    val googleMaps: String?,
    val openStreetMaps: String?
)

data class CarDTO(
    val signs: List<String>?,
    val side: String?
)
