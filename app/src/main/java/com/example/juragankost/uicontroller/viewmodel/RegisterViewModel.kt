package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState()) // Kita pinjam state LoginUiState aja biar praktis
        private set

    fun updateUiState(username: String = uiState.username, password: String = uiState.password) {
        uiState = uiState.copy(username = username, password = password)
    }

    fun register() {
        viewModelScope.launch {
            uiState = uiState.copy(loginStatus = LoginStatus.Loading)
            try {
                val response = repository.register(uiState.username, uiState.password)
                if (response.success) {
                    uiState = uiState.copy(loginStatus = LoginStatus.Success)
                } else {
                    uiState = uiState.copy(loginStatus = LoginStatus.Error(response.message))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(loginStatus = LoginStatus.Error("Gagal daftar: ${e.message}"))
            }
        }
    }

    fun resetStatus() {
        uiState = uiState.copy(loginStatus = LoginStatus.Idle)
    }
}