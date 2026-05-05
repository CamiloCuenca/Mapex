package com.mapex.features.countrydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapex.domain.model.Country
import com.mapex.domain.repository.CountryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CountryDetailState(
    val country: Country? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CountryDetailViewModel(
    private val repository: CountryRepository,
    private val countryCode: String?
) : ViewModel() {

    private val _state = MutableStateFlow(CountryDetailState())
    val state: StateFlow<CountryDetailState> = _state.asStateFlow()

    init {
        if (!countryCode.isNullOrBlank()) {
            loadCountryDetail(countryCode)
        }
    }

    private fun loadCountryDetail(code: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.getCountryByCode(code)
            result.onSuccess { country ->
                _state.value = _state.value.copy(
                    country = country,
                    isLoading = false
                )
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = error.message ?: "Error al cargar detalles"
                )
            }
        }
    }
}
