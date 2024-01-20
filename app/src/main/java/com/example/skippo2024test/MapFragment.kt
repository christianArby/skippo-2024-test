package com.example.skippo2024test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.skippo2024test.databinding.FragmentMapBinding
import com.example.skippo2024test.ui.navigate.NavigateCameraViewModel
import com.example.skippo2024test.ui.profile.ProfileCameraViewModel
import com.example.skippo2024test.ui.search.SearchCameraViewModel
import com.mapbox.maps.CameraChangedCallback
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.toCameraOptions
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private val mapViewModel: MapViewModel by activityViewModels()

    private val droppedPinFVM: DroppedPinFVM by activityViewModels()

    private val navigateCameraViewModel: NavigateCameraViewModel by activityViewModels()
    private val profileCameraViewModel: ProfileCameraViewModel by activityViewModels()
    private val searchCameraViewModel: SearchCameraViewModel by activityViewModels()

    private lateinit var binding: FragmentMapBinding


    data class MapUiState(
        val annotations: List<Annotation> = emptyList(),
        val lineLayer: LineLayer? = null
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMapBinding.inflate(inflater, container, false)

        val pointAnnotationManager =
            binding.mapboxMapView.annotations.createPointAnnotationManager()

        val map = binding.mapboxMapView
        map.mapboxMap.addOnMapClickListener { point ->
            droppedPinFVM.onMapClicked(point)
            true
        }

        lifecycleScope.launch {
            droppedPinFVM.droppedPinUiState.collect { point ->
                pointAnnotationManager.deleteAll()
                point?.let {
                    pointAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(
                                point
                            )
                            .withIconImage(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_home_black_24dp
                                )!!.toBitmap()
                            )
                    )
                }

            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    searchCameraViewModel.mapCameraState,
                    profileCameraViewModel.mapCameraState,
                    navigateCameraViewModel.mapCameraState
                ) { cameraStateArray ->
                    cameraStateArray.forEach { cameraState ->
                        cameraState?.let { map.mapboxMap.setCamera(cameraState.toCameraOptions()) }
                    }
                }.collect {
                    Log.d("MapFragment", "Camera state updated")
                }

            }
        }

        val callback = CameraChangedCallback { cameraChanged ->
            navigateCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
            profileCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
            searchCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
        }

        val cancelable = map.mapboxMap.subscribeCameraChanged(callback)

        return binding.root
    }
}