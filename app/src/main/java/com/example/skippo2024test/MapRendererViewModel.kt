package com.example.skippo2024test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class MapRendererViewModel (private val mapRendererStore: MapRendererStore) : ViewModel() {
    val isActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    abstract val renderableFeature: RenderableFeatures

    init {
        viewModelScope.launch {
            mapRendererStore.listOfActiveFeatures.map { features ->
                features.contains(renderableFeature)
            }.collect { isActive.value = it }
        }
    }
}