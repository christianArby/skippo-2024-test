package com.example.skippo2024test.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.skippo2024test.MainActivityViewModel
import com.example.skippo2024test.ui.dashboard.OverlayComposer
import com.example.skippo2024test.ui.dashboard.RenderableFeatures
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    companion object {
        val renderableFeatures = listOf<RenderableFeatures>()
    }

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