package com.mapex.core

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapex.data.local.AppDatabase
import com.mapex.data.repository.CountryRepositoryImpl
import com.mapex.features.countrydetail.CountryDetailScreen
import com.mapex.features.countrydetail.CountryDetailViewModel
import com.mapex.features.countrylist.CountryListScreen
import com.mapex.features.countrylist.CountryListViewModel
import com.mapex.ui.components.ConnectionStatusIndicator
import com.mapex.ui.theme.Screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CountryRepositoryImpl(database.countryDao()) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = MainRoutes.fromRoute(currentRoute)

    val showTopBar = currentRoute != "country_detail/{countryCode}"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text(text = currentScreen.title) },
                    actions = {
                        ConnectionStatusIndicator()
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = androidx.compose.material3.MaterialTheme
                            .colorScheme.surfaceContainerHigh
                    )
                )
            }
        }
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

            composable(MainRoutes.DetailCountries.route) {
                val viewModel = CountryListViewModel(repository)
                CountryListScreen(
                    viewModel = viewModel,
                    onCountrySelected = { countryCode ->
                        navController.navigate("country_detail/$countryCode")
                    }
                )
            }

            composable("country_detail/{countryCode}") { backStackEntry ->
                val countryCode = backStackEntry.arguments?.getString("countryCode")
                val viewModel = CountryDetailViewModel(repository, countryCode)
                CountryDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
