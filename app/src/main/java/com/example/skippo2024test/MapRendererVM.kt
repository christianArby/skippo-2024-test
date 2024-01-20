package com.example.skippo2024test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.ui.navigate.MapRendererStore
import com.example.skippo2024test.ui.navigate.RenderableFeatures
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class MapRendererVM: ViewModel() {
    abstract val isActive: StateFlow<Boolean>
}

@HiltViewModel
class DroppedPinFVM @Inject constructor(private val mapRendererStore: MapRendererStore) : MapRendererVM() {

    override val isActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _droppedPinUiState: MutableStateFlow<Point?> = MutableStateFlow(null)
    val droppedPinUiState: StateFlow<Point?> = isActive.flatMapLatest {
        if (it) {
            _droppedPinUiState
        } else {
            MutableStateFlow(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            mapRendererStore.listOfActiveFeatures.map { features ->
                features.contains(RenderableFeatures.DROPPED_PIN)
            }.collect { isActive.value = it }
        }
    }

    fun onMapClicked(point: Point) {
        if (isActive.value.not()) return
        _droppedPinUiState.value = point
    }

}