package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.Kamar
import com.example.juragankost.repositori.JuraganKostRepository
import com.example.juragankost.uicontroller.route.DestinasiDetailKost
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val kamarList: List<Kamar>) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailKostViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: JuraganKostRepository
) : ViewModel() {

    // Tangkap ID Kost dari Navigasi
    private val idKost: String = checkNotNull(savedStateHandle[DestinasiDetailKost.idKostArg])

    var detailUiState: DetailUiState by mutableStateOf(DetailUiState.Loading)
        private set

    init {
        getKamarList()
    }

    fun getKamarList() {
        viewModelScope.launch {
            detailUiState = DetailUiState.Loading
            try {
                val response = repository.getKamar(idKost)
                if (response.success) {
                    detailUiState = DetailUiState.Success(response.kamars)
                } else {
                    // Jika sukses tapi kosong, tetap anggap sukses (list kosong)
                    detailUiState = DetailUiState.Success(emptyList())
                }
            } catch (e: IOException) {
                detailUiState = DetailUiState.Error("Gagal memuat data. Periksa internet.")
            } catch (e: Exception) {
                detailUiState = DetailUiState.Error("Error: ${e.message}")
            }
        }
    }
    fun deleteKamar(idRoom: String) {
        viewModelScope.launch {
            try {
                repository.deleteKamar(idRoom)
                getKamarList() // Refresh grid setelah hapus
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}