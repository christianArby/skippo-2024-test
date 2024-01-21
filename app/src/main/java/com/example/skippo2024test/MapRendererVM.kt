package com.example.skippo2024test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class MapRendererVM: ViewModel() {
    abstract val isActive: StateFlow<Boolean>
}