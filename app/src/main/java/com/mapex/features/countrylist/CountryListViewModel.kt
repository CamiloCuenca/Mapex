package com.mapex.features.countrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.mapex.core.NetworkMonitor
import com.mapex.domain.model.Country
import com.mapex.domain.repository.CountryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class CountryListState(
    val countries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedContinent: String = "",
    val allContinents: List<String> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class CountryListViewModel(
    private val repository: CountryRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(CountryListState())
    val state: StateFlow<CountryListState> = _state.asStateFlow()

    val pagedCountries: Flow<PagingData<Country>> = _state
        .flatMapLatest { currentState ->
            repository.getPagedCountries(currentState.searchQuery)
                .map { pagingData ->
                    if (currentState.selectedContinent.isEmpty()) {
                        pagingData
                    } else {
                        pagingData.filter { country ->
                            country.continents.any {
                                it.equals(currentState.selectedContinent, ignoreCase = true)
                            }
                        }
                    }
                }
        }
        .cachedIn(viewModelScope)

    init {
        // Carga inicial
        loadAllCountries()

        // Escuchar cambios de conectividad:
        // Cuando el dispositivo pasa de offline → online, refrescamos desde la API.
        viewModelScope.launch {
            var wasOffline = false
            networkMonitor.isOnline.collect { isOnline ->
                if (isOnline && wasOffline) {
                    // Volvimos a tener internet: sincronizar datos frescos de la API
                    loadAllCountries()
                }
                wasOffline = !isOnline
            }
        }
    }

    fun reloadCountries() {
        loadAllCountries()
    }

    private fun loadAllCountries() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.getAllCountries()
            result.onSuccess { countries ->
                val continents = countries
                    .flatMap { it.continents }
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                _state.value = _state.value.copy(
                    countries = countries,
                    isLoading = false,
                    allContinents = continents
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error desconocido"
                )
            }
        }
    }

    fun searchByName(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun filterByContinent(continent: String) {
        _state.value = _state.value.copy(selectedContinent = continent)
    }

    /** Factory para inyectar dependencias sin Hilt/Koin. */
    class Factory(
        private val repository: CountryRepository,
        private val networkMonitor: NetworkMonitor
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CountryListViewModel(repository, networkMonitor) as T
        }
    }
}
