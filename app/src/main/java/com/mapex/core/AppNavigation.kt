package com.mapex.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.runtime.getValue

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { CountryRepositoryImpl(database.countryDao()) }
    // Un único NetworkMonitor compartido por toda la navegación
    val networkMonitor = remember { NetworkMonitor(context) }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showTopBar = currentRoute != "country_detail/{countryCode}"

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = MainRoutes.Home.route,
            modifier = Modifier.fillMaxSize(),
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
                // viewModel() + factory garantiza que el ViewModel sobreviva
                // recomposiciones y se cree solo una vez por destino de navegación.
                val viewModel: CountryListViewModel = viewModel(
                    factory = CountryListViewModel.Factory(repository, networkMonitor)
                )
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

        if (showTopBar) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.TopEnd
            ) {
                ConnectionStatusIndicator()
            }
        }
    }
}
