package com.example.skippo2024test.appscreens.navigate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skippo2024test.BottomNavItem
import com.example.skippo2024test.MainActivityViewModel
import com.example.skippo2024test.MapRendererStore
import com.example.skippo2024test.OverlayComposer
import com.example.skippo2024test.OverlayComposerVM
import com.example.skippo2024test.RenderableFeatures
import com.example.skippo2024test.features.DroppedPinFVM
import com.example.skippo2024test.modals.EditRouteMVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavigateFragment : Fragment() {
    private val viewModel: MainActivityViewModel by activityViewModels()
    private val overlayComposerVM: OverlayComposerVM by activityViewModels()
    private val droppedPinFVM: DroppedPinFVM by activityViewModels()
    private val editRouteMVM: EditRouteMVM by activityViewModels()

    companion object {
        val renderableFeatures = listOf(RenderableFeatures.DROPPED_PIN)
    }

    @Inject
    lateinit var mapRendererStore: MapRendererStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                OverlayComposer(droppedPinFVM, overlayComposerVM, editRouteMVM) {
                    viewModel.onBottomNavigationChanged(it)
                }
            }
        }
    }
}