package com.example.skippo2024test

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel: ViewModel() {

    val bottomNavItem: MutableStateFlow<BottomNavItem> = MutableStateFlow(BottomNavItem.PROFILE)

    fun onBottomNavigationChanged(bottomNavItem: BottomNavItem) {
        this.bottomNavItem.value = bottomNavItem
    }
}

enum class BottomNavItem {
    PROFILE,
    NAVIGATE,
    NOTIFICATIONS
}