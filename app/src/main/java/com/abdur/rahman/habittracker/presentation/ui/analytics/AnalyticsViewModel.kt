package com.abdur.rahman.habittracker.presentation.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdur.rahman.habittracker.domain.model.AnalyticsData
import com.abdur.rahman.habittracker.domain.usecase.GetAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalyticsUiState(
    val selectedPeriod: Int = 7,
    val analyticsData: AnalyticsData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()
    
    init {
        loadAnalytics()
    }
    
    private fun loadAnalytics() {
        viewModelScope.launch {
            getAnalyticsUseCase(_uiState.value.selectedPeriod)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { data ->
                    _uiState.update { it.copy(analyticsData = data, isLoading = false) }
                }
        }
    }
    
    fun selectPeriod(period: Int) {
        _uiState.update { it.copy(selectedPeriod = period, isLoading = true) }
        loadAnalytics()
    }
}
