package com.example.skippo2024test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class OverlayComposerVM: ViewModel() {

    val modalOverlay: MutableStateFlow<ModalOverlay?> = MutableStateFlow(null)

    fun setEditRoute() {
        modalOverlay.value = ModalOverlay.EDIT_ROUTE
    }
    fun clearModal() {
        modalOverlay.value = null
    }
}