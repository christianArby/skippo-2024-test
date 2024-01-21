package com.example.skippo2024test.modals

import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.MapRendererStore
import com.example.skippo2024test.MapRendererVM
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
class EditRouteMVM @Inject constructor(private val mapRendererStore: MapRendererStore): MapRendererVM() {

    companion object {
        val renderableFeatures = listOf(RenderableFeatures.EDIT_ROUTE)
    }

    override val isActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _editRoutePinUiState: MutableStateFlow<Point?> = MutableStateFlow(null)
    val editRoutePinUiState: StateFlow<Point?> = isActive.flatMapLatest {
        if (it) {
            _editRoutePinUiState
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
                features.contains(RenderableFeatures.EDIT_ROUTE)
            }.collect { isActive.value = it }
        }
    }

    fun onMapClicked(point: Point) {
        if (isActive.value.not()) return
        _editRoutePinUiState.value = point
    }

    fun clearEditRoute() {
        if (isActive.value.not()) return
        _editRoutePinUiState.value = null
    }

}