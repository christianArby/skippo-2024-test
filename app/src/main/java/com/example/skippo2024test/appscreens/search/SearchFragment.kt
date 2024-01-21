package com.example.skippo2024test.appscreens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.skippo2024test.MainActivityViewModel
import com.example.skippo2024test.OverlayComposerVM
import com.example.skippo2024test.appscreens.navigate.OverlayComposer
import com.example.skippo2024test.appscreens.navigate.RenderableFeatures
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val overlayComposerVM: OverlayComposerVM by viewModels()
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