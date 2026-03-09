package com.example.movieapp.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.data.repository.AccessibilityRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccessibilityViewModel(private val repository: AccessibilityRepositoryImpl) : ViewModel() {

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