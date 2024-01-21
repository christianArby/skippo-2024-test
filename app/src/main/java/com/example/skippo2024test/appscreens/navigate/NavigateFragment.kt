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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skippo2024test.BottomNavItem
import com.example.skippo2024test.DroppedPinFVM
import com.example.skippo2024test.EditRouteMVM
import com.example.skippo2024test.MainActivityViewModel
import com.example.skippo2024test.OverlayComposerVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MapRendererStore @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Main)

    val listOfActiveFeaturesForAppScreen: MutableStateFlow<List<RenderableFeatures>> =
        MutableStateFlow<List<RenderableFeatures>>(listOf())

    val listOfActiveFeaturesForModal: MutableStateFlow<List<RenderableFeatures>?> =
        MutableStateFlow<List<RenderableFeatures>?>(listOf())

    val listOfActiveFeatures: StateFlow<List<RenderableFeatures>> = combine(
        listOfActiveFeaturesForModal,
        listOfActiveFeaturesForAppScreen
    ) { modalList, appScreenList ->
        modalList ?: appScreenList
    }.stateIn(scope, SharingStarted.Lazily, emptyList())

    fun setActiveFeatures(features: List<RenderableFeatures>) {
        listOfActiveFeaturesForAppScreen.value = features
    }

    fun setActiveFeaturesForModal(features: List<RenderableFeatures>?) {
        listOfActiveFeaturesForModal.value = features
    }
}


enum class RenderableFeatures {
    DROPPED_PIN,
    EDIT_ROUTE
}

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

@Composable
fun OverlayComposer(
    droppedPinFVM: DroppedPinFVM = viewModel(),
    overlayComposerVM: OverlayComposerVM = viewModel(),
    editRouteMVM: EditRouteMVM = viewModel(),
    bottomNavChanged : (bottomNav: BottomNavItem) -> Unit
) {

    val overlay = overlayComposerVM.modalOverlay.collectAsState()

    Box() {
        ModalOverlayContainer(overlay.value, overlayComposerVM, editRouteMVM)
    }
    if (overlay.value == null) Box(contentAlignment = Alignment.BottomCenter) {
        BottomSheet(droppedPinFVM, overlayComposerVM)
        Column(verticalArrangement = Arrangement.Bottom) {
            BottomNavigationBar(bottomNavChanged)
        }
    }


}

@Composable
fun ModalOverlayContainer(modalOverlay: ModalOverlay?, overlayComposerVM: OverlayComposerVM = viewModel(), editRouteMVM: EditRouteMVM = viewModel()) {
    modalOverlay?.let {
        EditRouteModalOverlay(overlayComposerVM, editRouteMVM)
    }
}

@Composable
fun EditRouteModalOverlay(
    overlayComposerVM: OverlayComposerVM = viewModel(),
    editRouteMVM: EditRouteMVM = viewModel()
) {
    Box(modifier = Modifier
        .background(color = Color.White)
        .height(200.dp)
        .fillMaxWidth()) {
        Column {
            Text(text = "Editing route")
            Button(onClick = {
                editRouteMVM.clearEditRoute()
                overlayComposerVM.clearModal() }) {
                Text(text = "Done editing")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(droppedPinFVM: DroppedPinFVM = viewModel(), overlayComposerVM: OverlayComposerVM = viewModel()) {

    val point by droppedPinFVM.droppedPinUiState.collectAsState()
    val initialValue = point?.let { SheetValue.Expanded } ?: SheetValue.Hidden

    val sheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(skipPartiallyExpanded = false, initialValue = initialValue),
    )

    LaunchedEffect(sheetState.bottomSheetState.currentValue) {
        snapshotFlow { sheetState.bottomSheetState.currentValue }.collect { currentValue ->
            if (currentValue != SheetValue.Expanded) {
                droppedPinFVM.clearDroppedPin()
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetContent = {
            Text(text = point.toString())
            Button(onClick = { overlayComposerVM.setEditRoute() }) {
                Text(text = "Create route")
            }
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
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.PROFILE) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Navigate"
                )
            },
            label = { Text("Navigate") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.NAVIGATE) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            label = { Text("Search") },
            selected = true,
            onClick = { bottomNavChanged(BottomNavItem.SEARCH) }
        )
    }
}

enum class ModalOverlay {
    EDIT_ROUTE
}