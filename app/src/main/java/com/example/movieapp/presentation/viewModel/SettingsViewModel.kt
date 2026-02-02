package com.example.movieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.repository.SettingsRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepositoryImpl) : ViewModel() {

    val isHighContrastEnabled: StateFlow<Boolean> = repository.isHighContrastEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val fontScale: StateFlow<Float> = repository.fontScale
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 1.0f
        )


    fun onHighContrastToggled(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleHighContrast(enabled)
        }
    }


    fun onFontScaleChanged(newScale: Float) {
        viewModelScope.launch {
            repository.updateFontScale(newScale)
        }
    }
}