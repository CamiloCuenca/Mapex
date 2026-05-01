package com.mapex.core

sealed class MainRoutes(
    val route: String,
    val title: String,
    val shortLabel: String,
) {
    data object Home : MainRoutes(
        route = "home",
        title = "Home",
        shortLabel = "H",
    )

    data object DetailCountries : MainRoutes(
        route = "detail_countries",
        title = "Detail Countries",
        shortLabel = "D",
    )

    companion object {
        val bottomItems = listOf(Home, DetailCountries)

        fun fromRoute(route: String?): MainRoutes = when (route) {
            DetailCountries.route -> DetailCountries
            else -> Home
        }
    }
}