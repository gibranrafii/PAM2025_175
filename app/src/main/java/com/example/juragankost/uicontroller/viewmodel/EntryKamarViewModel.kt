package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch

data class EntryKamarUiState(
    val nomorKamar: String = "",
    val harga: String = "",
    val isEntryValid: Boolean = false,
    val submitStatus: SubmitStatus = SubmitStatus.Idle
)

class EntryKamarViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var uiState by mutableStateOf(EntryKamarUiState())
        private set

    fun updateUiState(nomor: String = uiState.nomorKamar, harga: String = uiState.harga) {
        uiState = uiState.copy(
            nomorKamar = nomor,
            harga = harga,
            isEntryValid = nomor.isNotBlank() && harga.isNotBlank()
        )
    }

    fun saveKamar(idKost: String) {
        viewModelScope.launch {
            uiState = uiState.copy(submitStatus = SubmitStatus.Loading)
            try {
                val response = repository.addKamar(idKost, uiState.nomorKamar, uiState.harga)
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

    fun resetForm() {
        uiState = EntryKamarUiState()
    }
}