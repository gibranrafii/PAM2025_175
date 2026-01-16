package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.Payment
import com.example.juragankost.repositori.JuraganKostRepository // Import yang Benar
import kotlinx.coroutines.launch
import java.io.IOException

// UI State (Tetap Sama)
data class RiwayatKostUiState(
    val paymentList: List<Payment> = listOf(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

// Gunakan JuraganKostRepository di sini
class RiwayatKostViewModel(private val repository: JuraganKostRepository) : ViewModel() {
    var uiState by mutableStateOf(RiwayatKostUiState())
        private set

    fun getRiwayatByKost(idKost: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // PERBAIKAN: Panggil API Asli
                val response = repository.getRiwayatKost(idKost)

                // Masukkan data dari API ke UI State
                uiState = RiwayatKostUiState(
                    paymentList = response.payments, // Menggunakan data asli
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = RiwayatKostUiState(
                    isError = true,
                    errorMessage = e.message ?: "Error",
                    isLoading = false
                )
            }
        }
    }
}