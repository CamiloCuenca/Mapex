package com.mapex.data.repository

import com.mapex.data.local.entity.ContriesEntity
import com.mapex.data.local.entity.PaisDetalleEntity
import com.mapex.domain.model.Country

fun Country.toEntity(): ContriesEntity {
    return ContriesEntity(
        id_contries = id,
        nombre_comun = commonName,
        region = region,
        poblacion = population,
        url_banera_png = flags ?: "",
        continentes = continents.joinToString(",")
    )
}

fun Country.toDetailEntity(): PaisDetalleEntity {
    return PaisDetalleEntity(
        id_contries = id,
        nombre_oficial = officialName,
        subregion = subregion,
        superficie_km2 = area ?: 0.0,
        capital = capital.firstOrNull() ?: "N/A",
        moneda_texto = currencies.joinToString(", "),
        idioma_texto = languages.joinToString(", "),
        enlace_map = googleMapsUrl ?: "",
        escudo_url = coatOfArms,
        fronteras = borders.joinToString(","),
        zonas_horarias = timezones.joinToString(","),
        latitud_longitud = latlng.joinToString(","),
        codigo_llamada = callingCode,
        tld = tld.joinToString(","),
        landlocked = landlocked,
        car_side = carSide
    )
}

fun ContriesEntity.toDomain(detail: PaisDetalleEntity? = null): Country {
    return Country(
        id = id_contries,
        commonName = nombre_comun,
        officialName = detail?.nombre_oficial ?: nombre_comun,
        region = region,
        subregion = detail?.subregion ?: "N/A",
        capital = if (detail != null) listOf(detail.capital) else emptyList(),
        population = poblacion,
        area = detail?.superficie_km2,
        timezones = detail?.zonas_horarias?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        languages = detail?.idioma_texto?.split(", ") ?: emptyList(),
        currencies = detail?.moneda_texto?.split(", ") ?: emptyList(),
        flags = url_banera_png,
        codeAlpha2 = id_contries.take(2),
        codeAlpha3 = id_contries,
        coatOfArms = detail?.escudo_url,
        borders = detail?.fronteras?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        tld = detail?.tld?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        callingCode = detail?.codigo_llamada,
        googleMapsUrl = detail?.enlace_map,
        latlng = detail?.latitud_longitud?.split(",")?.filter { it.isNotBlank() }?.mapNotNull { it.toDoubleOrNull() } ?: emptyList(),
        landlocked = detail?.landlocked ?: false,
        carSide = detail?.car_side,
        continents = if (continentes.isBlank()) emptyList() else continentes.split(",")
    )
}
