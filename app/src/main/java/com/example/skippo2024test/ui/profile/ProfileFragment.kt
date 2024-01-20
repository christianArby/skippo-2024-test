package com.example.skippo2024test.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skippo2024test.MainActivityViewModel
import com.example.skippo2024test.ui.navigate.OverlayComposer
import com.example.skippo2024test.ui.navigate.MapRendererStore
import com.example.skippo2024test.ui.navigate.RenderableFeatures
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    companion object {
        val renderableFeatures = listOf<RenderableFeatures>()
    }
    @Inject
    lateinit var mapRendererStore: MapRendererStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                OverlayComposer() {
                    mainActivityViewModel.onBottomNavigationChanged(it)
                }
            }
        }
    }
}