package com.example.skippo2024test

import android.animation.ValueAnimator
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
import com.example.skippo2024test.ui.dashboard.DashboardCameraViewModel
import com.example.skippo2024test.ui.home.HomeCameraViewModel
import com.example.skippo2024test.ui.notifications.NotificationsCameraViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraChangedCallback
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.plugin.animation.CameraAnimationsLifecycleListener
import com.mapbox.maps.plugin.animation.CameraAnimatorChangeListener
import com.mapbox.maps.plugin.animation.CameraAnimatorType
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.ViewportStatusObserver
import com.mapbox.maps.plugin.viewport.data.ViewportStatusChangeReason
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.maps.toCameraOptions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MapFragment : Fragment() {

    private val mapViewModel: MapViewModel by activityViewModels()

    private val droppedPinFVM: DroppedPinFVM by activityViewModels()

    private val dashboardCameraViewModel: DashboardCameraViewModel by activityViewModels()
    private val homeCameraViewModel: HomeCameraViewModel by activityViewModels()
    private val notificationsCameraViewModel: NotificationsCameraViewModel by activityViewModels()

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
                    notificationsCameraViewModel.mapCameraState,
                    homeCameraViewModel.mapCameraState,
                    dashboardCameraViewModel.mapCameraState
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
            dashboardCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
            homeCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
            notificationsCameraViewModel.cameraStateUpdated(map.mapboxMap.cameraState)
        }

        val cancelable = map.mapboxMap.subscribeCameraChanged(callback)

        return binding.root
    }
}