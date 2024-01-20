package com.example.skippo2024test.ui.notifications

import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.MapCamera
import com.example.skippo2024test.ui.navigate.CameraViewModel
import com.mapbox.maps.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NotificationsCameraViewModel: CameraViewModel() {
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
        isActive.value = camera == MapCamera.NOTIFICATIONS
    }

    fun cameraStateUpdated(cameraState: CameraState) {
        if (isActive.value.not()) return
        this.cameraState.value = cameraState
    }
}