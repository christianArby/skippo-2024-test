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
import com.example.skippo2024test.appscreens.navigate.NavigateCameraViewModel
import com.example.skippo2024test.appscreens.profile.ProfileCameraViewModel
import com.example.skippo2024test.appscreens.search.SearchCameraViewModel
import com.example.skippo2024test.features.DroppedPinFVM
import com.example.skippo2024test.modals.EditRouteMVM
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
    private val droppedPinFVM: DroppedPinFVM by activityViewModels()
    private val editRouteMVM: EditRouteMVM by activityViewModels()

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

        val droppedPintAnnotationManager =
            binding.mapboxMapView.annotations.createPointAnnotationManager()
        val editRouteAnnotationManager =
            binding.mapboxMapView.annotations.createPointAnnotationManager()

        val map = binding.mapboxMapView
        map.mapboxMap.addOnMapClickListener { point ->
            droppedPinFVM.onMapClicked(point)
            editRouteMVM.onMapClicked(point)
            true
        }

        lifecycleScope.launch {
            droppedPinFVM.droppedPinUiState.collect { point ->
                droppedPintAnnotationManager.deleteAll()
                point?.let {
                    droppedPintAnnotationManager.create(
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
            editRouteMVM.editRoutePinUiState.collect { point ->
                point?.let {
                    editRouteAnnotationManager.create(
                        PointAnnotationOptions()
                            .withPoint(
                                point
                            )
                            .withIconImage(
                                ContextCompat.getDrawable(
                                    requireContext(),
                                    R.drawable.ic_notifications_black_24dp
                                )!!.toBitmap()
                            )
                    )
                } ?: run {
                    editRouteAnnotationManager.deleteAll()
                }
            }
        }

        lifecycleScope.launch {
            droppedPinFVM.droppedPinUiState.collect { point ->
                droppedPintAnnotationManager.deleteAll()
                point?.let {
                    droppedPintAnnotationManager.create(
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
            notifyCameras()
        }

        val cancelable = map.mapboxMap.subscribeCameraChanged(callback)

        return binding.root
    }

    fun notifyCameras() {
        navigateCameraViewModel.cameraStateUpdated(binding.mapboxMapView.mapboxMap.cameraState)
        profileCameraViewModel.cameraStateUpdated(binding.mapboxMapView.mapboxMap.cameraState)
        searchCameraViewModel.cameraStateUpdated(binding.mapboxMapView.mapboxMap.cameraState)
    }
}