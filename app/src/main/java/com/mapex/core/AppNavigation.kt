package com.mapex.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapex.data.repository.CountryRepositoryImpl
import com.mapex.features.countrydetail.CountryDetailScreen
import com.mapex.features.countrydetail.CountryDetailViewModel
import com.mapex.features.countrylist.CountryListScreen
import com.mapex.features.countrylist.CountryListViewModel
import com.mapex.ui.theme.Screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = MainRoutes.fromRoute(currentRoute)

    // Hide the shared TopAppBar on detail screen which has its own TopAppBar
    val showTopBar = currentRoute != "country_detail/{countryCode}"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(text = currentScreen.title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        // M3 surface container high for the bar background
                        containerColor = androidx.compose.material3.MaterialTheme
                            .colorScheme.surfaceContainerHigh
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar {
                MainRoutes.bottomItems.forEach { screen ->
                    val selected = when {
                        screen == MainRoutes.DetailCountries &&
                                (currentRoute == screen.route || currentRoute == "countries" ||
                                        currentRoute == "country_detail/{countryCode}") -> true

                        else -> currentRoute == screen.route
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            // M3 NavigationBarItem with real vector icons
                            when (screen) {
                                MainRoutes.Home -> Icon(
                                    Icons.Default.Home,
                                    contentDescription = screen.title
                                )

                                MainRoutes.DetailCountries -> Icon(
                                    Icons.Default.Map,
                                    contentDescription = screen.title
                                )
                            }
                        },
                        label = { Text(screen.title) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(MainRoutes.Home.route) {
                HomeScreen(
                    onNavigateToCountries = {
                        navController.navigate(MainRoutes.DetailCountries.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // DetailCountries tab now shows the country list directly
            composable(MainRoutes.DetailCountries.route) {
                val viewModel = CountryListViewModel(CountryRepositoryImpl)
                CountryListScreen(
                    viewModel = viewModel,
                    onCountrySelected = { countryCode ->
                        navController.navigate("country_detail/$countryCode")
                    }
                )
            }

            composable("country_detail/{countryCode}") { backStackEntry ->
                val countryCode = backStackEntry.arguments?.getString("countryCode")
                val viewModel = CountryDetailViewModel(CountryRepositoryImpl, countryCode)
                CountryDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

