package com.plataforma.bienestar.gestor

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

class ViewModelGestor : ViewModel() {
    private val _selectedTab = mutableStateOf("perfil")
    val selectedTab: State<String> = _selectedTab

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }
}