package com.example.skippo2024test.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skippo2024test.BottomNavItem
import com.example.skippo2024test.DroppedPinFVM
import com.example.skippo2024test.MainActivityViewModel
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MapRendererStore @Inject constructor() {
    val listOfActiveFeatures: MutableStateFlow<List<RenderableFeatures>> =
        MutableStateFlow(listOf())
    fun setActiveFeatures(features: List<RenderableFeatures>) {
        listOfActiveFeatures.value = features
    }
}


enum class RenderableFeatures {
    DROPPED_PIN
}

@AndroidEntryPoint
class DashboardFragment : Fragment() {


    private val viewModel: MainActivityViewModel by activityViewModels()

    private val droppedPinFVM: DroppedPinFVM by activityViewModels()

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
                OverlayComposer(droppedPinFVM) {
                    viewModel.onBottomNavigationChanged(it)
                }
            }
        }
    }
}

@Preview(heightDp = 600, widthDp = 320)
@Composable
fun DashboardPreview() {
    OverlayComposer() {}
}

@Composable
fun OverlayComposer(droppedPinFVM: DroppedPinFVM = viewModel(), bottomNavChanged : (bottomNav: BottomNavItem) -> Unit) {

    BottomSheet(droppedPinFVM)
    Column(verticalArrangement = Arrangement.Bottom) {
        BottomNavigationBar(bottomNavChanged)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(droppedPinFVM: DroppedPinFVM = viewModel()) {

    val point by droppedPinFVM.droppedPinUiState.collectAsState()
    val initialValue = point?.let { SheetValue.Expanded } ?: SheetValue.Hidden

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(skipPartiallyExpanded = false, initialValue = initialValue),
    )

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            Text(text = point.toString())
            Spacer(modifier = Modifier.height(200.dp))
        }) {
    }
}

@Composable
fun BottomNavigationBar(bottomNavChanged : (bottomNav: BottomNavItem) -> Unit) {
    BottomNavigation(backgroundColor = Color.White) {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.HOME) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text("Dashboard") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.DASHBOARD) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            },
            label = { Text("Notifications") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.NOTIFICATIONS) }
        )
    }
}