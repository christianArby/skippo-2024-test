package com.example.skippo2024test

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.skippo2024test.appscreens.navigate.ModalOverlay
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