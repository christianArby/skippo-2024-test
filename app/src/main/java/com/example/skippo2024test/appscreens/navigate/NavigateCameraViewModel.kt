package com.example.skippo2024test.appscreens.navigate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skippo2024test.MapCamera
import com.example.skippo2024test.appscreens.MapCameraViewModel
import com.mapbox.maps.CameraState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NavigateCameraViewModel : MapCameraViewModel() {
    override val mapCamera: MapCamera = MapCamera.NAVIGATE
}