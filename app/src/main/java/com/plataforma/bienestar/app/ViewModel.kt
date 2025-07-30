package com.plataforma.bienestar.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class TabViewModel : ViewModel() {
    private val _selectedTab = mutableStateOf("home")
    val selectedTab: State<String> = _selectedTab

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }
}