package com.example.skippo2024test

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skippo2024test.features.DroppedPinFVM
import com.example.skippo2024test.modals.EditRouteMVM

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

enum class ModalOverlay {
    EDIT_ROUTE
}