package com.example.skippo2024test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.skippo2024test.databinding.ActivityMainBinding
import com.example.skippo2024test.ui.dashboard.CameraViewModel
import com.example.skippo2024test.ui.dashboard.DashboardCameraViewModel
import com.example.skippo2024test.ui.dashboard.DashboardFragment
import com.example.skippo2024test.ui.dashboard.MapRendererStore
import com.example.skippo2024test.ui.home.HomeCameraViewModel
import com.example.skippo2024test.ui.home.HomeFragment
import com.example.skippo2024test.ui.notifications.NotificationsCameraViewModel
import com.example.skippo2024test.ui.notifications.NotificationsFragment
import com.mapbox.common.MapboxOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

enum class MapCamera {
    HOME,
    DASHBOARD,
    NOTIFICATIONS
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mapFragment = MapFragment()

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private val dashboardCameraViewModel: DashboardCameraViewModel by viewModels()
    private val homeCameraViewModel: HomeCameraViewModel by viewModels()
    private val notificationsCameraViewModel: NotificationsCameraViewModel by viewModels()

    private val homeFragment = HomeFragment()
    private val dashboardFragment = DashboardFragment()
    private val notificationsFragment = NotificationsFragment()

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mapRendererStore: MapRendererStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOfCameras = listOf(dashboardCameraViewModel, homeCameraViewModel, notificationsCameraViewModel)

        MapboxOptions.accessToken = ""

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.map_container, mapFragment).commit()

        supportFragmentManager.beginTransaction()
            .add(R.id.app_screen_container, homeFragment)
            .add(R.id.app_screen_container, dashboardFragment)
            .add(R.id.app_screen_container, notificationsFragment)
            .hide(homeFragment)
            .hide(dashboardFragment)
            .hide(notificationsFragment).commit()


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.bottomNavItem.collect { item ->
                    when (item) {
                        BottomNavItem.HOME -> {
                            mapRendererStore.setActiveFeatures(HomeFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.HOME)
                            }
                            supportFragmentManager.beginTransaction().show(homeFragment).hide(dashboardFragment).hide(notificationsFragment).commit()
                        }
                        BottomNavItem.DASHBOARD -> {
                            mapRendererStore.setActiveFeatures(DashboardFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.DASHBOARD)
                            }
                            supportFragmentManager.beginTransaction().show(dashboardFragment).hide(homeFragment).hide(notificationsFragment).commit()
                        }
                        BottomNavItem.NOTIFICATIONS -> {
                            mapRendererStore.setActiveFeatures(NotificationsFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.NOTIFICATIONS)
                            }
                            supportFragmentManager.beginTransaction().show(notificationsFragment).hide(homeFragment).hide(dashboardFragment).commit()
                        }
                    }
                }
            }
        }
    }
}