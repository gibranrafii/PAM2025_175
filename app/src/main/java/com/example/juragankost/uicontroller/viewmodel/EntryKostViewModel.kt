package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch

// State untuk Form Input
data class EntryKostUiState(
    val namaKost: String = "",
    val alamat: String = "",
    val isEntryValid: Boolean = false,
    val submitStatus: SubmitStatus = SubmitStatus.Idle
)

sealed interface SubmitStatus {
    object Idle : SubmitStatus
    object Loading : SubmitStatus
    object Success : SubmitStatus
    data class Error(val message: String) : SubmitStatus
}

class EntryKostViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var uiState by mutableStateOf(EntryKostUiState())
        private set

    // Update state saat user mengetik
    fun updateUiState(nama: String = uiState.namaKost, alamat: String = uiState.alamat) {
        uiState = uiState.copy(
            namaKost = nama,
            alamat = alamat,
            isEntryValid = nama.isNotBlank() && alamat.isNotBlank()
        )
    }

    // Fungsi Simpan Kost
    fun saveKost(idUser: String) {
        viewModelScope.launch {
            uiState = uiState.copy(submitStatus = SubmitStatus.Loading)
            try {
                val response = repository.addKost(idUser, uiState.namaKost, uiState.alamat)
                if (response.success) {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Success)
                } else {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Error(response.message))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(submitStatus = SubmitStatus.Error("Gagal menyimpan: ${e.message}"))
            }
        }
    }

    // Reset form setelah sukses
    fun resetForm() {
        uiState = EntryKostUiState()
    }


}