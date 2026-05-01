package com.mapex.core

sealed class MainRoutes(
    val route: String,
    val title: String,
    val shortLabel: String,
) {
    data object Home : MainRoutes(
        route = "home",
        title = "Inicio",
        shortLabel = "H",
    )

    data object DetailCountries : MainRoutes(
        route = "detail_countries",
        title = "Países",
        shortLabel = "P",
    )

    companion object {
        val bottomItems = listOf(Home, DetailCountries)

        fun fromRoute(route: String?): MainRoutes = when {
            route == DetailCountries.route ||
                    route == "countries" ||
                    route?.startsWith("country_detail") == true -> DetailCountries

            else -> Home
        }
    }
}