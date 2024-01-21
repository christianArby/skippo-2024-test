package com.example.skippo2024test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRendererStore @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Main)

    val listOfActiveFeaturesForAppScreen: MutableStateFlow<List<RenderableFeatures>> =
        MutableStateFlow<List<RenderableFeatures>>(listOf())

    val listOfActiveFeaturesForModal: MutableStateFlow<List<RenderableFeatures>?> =
        MutableStateFlow<List<RenderableFeatures>?>(listOf())

    val listOfActiveFeatures: StateFlow<List<RenderableFeatures>> = combine(
        listOfActiveFeaturesForModal,
        listOfActiveFeaturesForAppScreen
    ) { modalList, appScreenList ->
        modalList ?: appScreenList
    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    fun setActiveFeatures(features: List<RenderableFeatures>) {
        listOfActiveFeaturesForAppScreen.value = features
    }

    fun setActiveFeaturesForModal(features: List<RenderableFeatures>?) {
        listOfActiveFeaturesForModal.value = features
    }
}


enum class RenderableFeatures {
    DROPPED_PIN,
    EDIT_ROUTE
}