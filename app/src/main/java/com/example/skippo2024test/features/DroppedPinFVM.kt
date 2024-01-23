package com.example.skippo2024test.features

import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.MapRendererStore
import com.example.skippo2024test.MapRendererViewModel
import com.example.skippo2024test.RenderableFeatures
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

@HiltViewModel
class DroppedPinFVM @Inject constructor(mapRendererStore: MapRendererStore) : MapRendererViewModel(mapRendererStore) {

    override val renderableFeature: RenderableFeatures = RenderableFeatures.DROPPED_PIN

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

    fun onMapClicked(point: Point) {
        if (isActive.value.not()) return
        _droppedPinUiState.value = point
    }

    fun clearDroppedPin() {
        if (isActive.value.not()) return
        _droppedPinUiState.value = null
    }

}