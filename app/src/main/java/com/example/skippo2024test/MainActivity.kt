package com.example.skippo2024test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.skippo2024test.databinding.ActivityMainBinding
import com.example.skippo2024test.appscreens.navigate.NavigateCameraViewModel
import com.example.skippo2024test.appscreens.navigate.NavigateFragment
import com.example.skippo2024test.appscreens.navigate.MapRendererStore
import com.example.skippo2024test.appscreens.navigate.ModalOverlay
import com.example.skippo2024test.appscreens.search.SearchCameraViewModel
import com.example.skippo2024test.appscreens.search.SearchFragment
import com.example.skippo2024test.appscreens.profile.ProfileCameraViewModel
import com.example.skippo2024test.appscreens.profile.ProfileFragment
import com.mapbox.common.MapboxOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MapCamera {
    PROFILE,
    NAVIGATE,
    SEARCH
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mapFragment = MapFragment()

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private val navigateCameraViewModel: NavigateCameraViewModel by viewModels()
    private val profileCameraViewModel: ProfileCameraViewModel by viewModels()
    private val searchCameraViewModel: SearchCameraViewModel by viewModels()

    private val profileFragment = ProfileFragment()
    private val navigateFragment = NavigateFragment()
    private val searchFragment = SearchFragment()

    private val overlayComposerVM: OverlayComposerVM by viewModels()

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var mapRendererStore: MapRendererStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOfCameras = listOf(navigateCameraViewModel, profileCameraViewModel, searchCameraViewModel)

        MapboxOptions.accessToken = ""

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.map_container, mapFragment).commit()

        supportFragmentManager.beginTransaction()
            .add(R.id.app_screen_container, profileFragment)
            .add(R.id.app_screen_container, navigateFragment)
            .add(R.id.app_screen_container, searchFragment)
            .hide(profileFragment)
            .hide(navigateFragment)
            .hide(searchFragment).commit()


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.bottomNavItem.collect { item ->
                    when (item) {
                        BottomNavItem.PROFILE -> {
                            mapRendererStore.setActiveFeatures(ProfileFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.PROFILE)
                            }
                            supportFragmentManager.beginTransaction().show(profileFragment).hide(navigateFragment).hide(searchFragment).commit()
                        }
                        BottomNavItem.NAVIGATE -> {
                            mapRendererStore.setActiveFeatures(NavigateFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.NAVIGATE)
                            }
                            supportFragmentManager.beginTransaction().show(navigateFragment).hide(profileFragment).hide(searchFragment).commit()
                        }
                        BottomNavItem.SEARCH -> {
                            mapRendererStore.setActiveFeatures(SearchFragment.renderableFeatures)
                            listOfCameras.forEach {
                                it.notifyActiveCamera(MapCamera.SEARCH)
                            }
                            supportFragmentManager.beginTransaction().show(searchFragment).hide(profileFragment).hide(navigateFragment).commit()
                        }
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                overlayComposerVM.modalOverlay.collect { modalOverlay ->
                    when (modalOverlay) {
                        ModalOverlay.EDIT_ROUTE -> {
                            mapRendererStore.setActiveFeaturesForModal(EditRouteMVM.renderableFeatures)
                        }
                        null -> {
                            mapRendererStore.setActiveFeaturesForModal(null)
                        }
                    }
                }
            }
        }
    }
}