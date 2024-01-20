package com.example.skippo2024test.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.MapCamera
import com.mapbox.maps.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class CameraViewModel : ViewModel() {
    abstract fun notifyActiveCamera(camera: MapCamera)
}

class DashboardCameraViewModel : CameraViewModel() {
    val isActive: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val mapCameraState: MutableStateFlow<CameraState?> = MutableStateFlow(null)

    val cameraState: MutableStateFlow<CameraState?> = MutableStateFlow(null)

    init {
        viewModelScope.launch {
            isActive.collect {
                if (!it) {
                    mapCameraState.value = null
                }
                if (it) {
                    mapCameraState.value = cameraState.value
                }
            }
        }
    }

    override fun notifyActiveCamera(camera: MapCamera) {
        isActive.value = camera == MapCamera.DASHBOARD
    }

    fun cameraStateUpdated(cameraState: CameraState) {
        if (isActive.value.not()) return
        this.cameraState.value = cameraState
    }
}