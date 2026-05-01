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
    val selectedContinent: String = "",
    val allContinents: List<String> = emptyList()
)

class CountryListViewModel(
    private val repository: CountryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CountryListState())
    val state: StateFlow<CountryListState> = _state.asStateFlow()

    init {
        loadAllCountries()
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
                applyFilters()
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
    }

    fun filterByContinent(continent: String) {
        _state.value = _state.value.copy(selectedContinent = continent)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _state.value.searchQuery.trim().lowercase()
        val selectedContinent = _state.value.selectedContinent
        val filtered = _state.value.countries.filter { country ->
            val matchesQuery = query.isEmpty() ||
                country.commonName.lowercase().contains(query) ||
                country.officialName.lowercase().contains(query)

            val matchesContinent = selectedContinent.isEmpty() ||
                country.continents.any { it.equals(selectedContinent, ignoreCase = true) }

            matchesQuery && matchesContinent
        }
        _state.value = _state.value.copy(filteredCountries = filtered)
    }
}
