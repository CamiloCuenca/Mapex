package com.mapex.features.countrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapex.domain.model.Country
import com.mapex.domain.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CountryListState(
    val countries: List<Country> = emptyList(),
    val filteredCountries: List<Country> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRegion: String = "",
    val allRegions: List<String> = emptyList()
)

class CountryListViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CountryListState())
    val state: StateFlow<CountryListState> = _state.asStateFlow()

    init {
        loadAllCountries()
    }

    private fun loadAllCountries() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.getAllCountries()
            result.onSuccess { countries ->
                val regions = countries.map { it.region }.distinct().sorted()
                _state.value = _state.value.copy(
                    countries = countries,
                    filteredCountries = countries,
                    isLoading = false,
                    allRegions = regions
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
        applyFilters()

        if (query.isNotBlank()) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val result = repository.searchCountriesByName(query)
                result.onSuccess { countries ->
                    _state.value = _state.value.copy(
                        countries = countries,
                        filteredCountries = countries,
                        isLoading = false
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error en la búsqueda"
                    )
                }
            }
        } else {
            loadAllCountries()
        }
    }

    fun filterByRegion(region: String) {
        _state.value = _state.value.copy(selectedRegion = region)
        if (region.isNotBlank()) {
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true, error = null)
                val result = repository.getCountriesByRegion(region)
                result.onSuccess { countries ->
                    _state.value = _state.value.copy(
                        countries = countries,
                        filteredCountries = countries,
                        isLoading = false
                    )
                }.onFailure { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al filtrar"
                    )
                }
            }
        } else {
            loadAllCountries()
        }
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery.lowercase()
        val region = _state.value.selectedRegion
        val filtered = _state.value.countries.filter { country ->
            val matchesQuery = country.commonName.lowercase().contains(query) ||
                    country.officialName.lowercase().contains(query)
            val matchesRegion = region.isEmpty() || country.region == region
            matchesQuery && matchesRegion
        }
        _state.value = _state.value.copy(filteredCountries = filtered)
    }
}
