package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.Penghuni
import com.example.juragankost.repositori.JuraganKostRepository
import com.example.juragankost.uicontroller.route.DestinasiDetailPenghuni
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DetailPenghuniUiState {
    object Loading : DetailPenghuniUiState
    data class Success(val penghuni: Penghuni) : DetailPenghuniUiState
    data class Error(val message: String) : DetailPenghuniUiState
}

class DetailPenghuniViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: JuraganKostRepository
) : ViewModel() {

    private val idRoom: String = checkNotNull(savedStateHandle[DestinasiDetailPenghuni.idRoomArg])

    var uiState: DetailPenghuniUiState by mutableStateOf(DetailPenghuniUiState.Loading)
        private set

    // Status khusus untuk proses Check-Out
    var checkoutStatus: SubmitStatus by mutableStateOf(SubmitStatus.Idle)
        private set

    init {
        getDetailPenghuni()
    }

    fun getDetailPenghuni() {
        viewModelScope.launch {
            uiState = DetailPenghuniUiState.Loading
            try {
                val response = repository.getPenghuni(idRoom)
                if (response.success && response.data != null) {
                    uiState = DetailPenghuniUiState.Success(response.data)
                } else {
                    uiState = DetailPenghuniUiState.Error("Data tidak ditemukan")
                }
            } catch (e: IOException) {
                uiState = DetailPenghuniUiState.Error("Gagal memuat data. Cek koneksi.")
            }
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            checkoutStatus = SubmitStatus.Loading
            try {
                val response = repository.checkOut(idRoom)
                if (response.success) {
                    checkoutStatus = SubmitStatus.Success
                } else {
                    checkoutStatus = SubmitStatus.Error(response.message)
                }
            } catch (e: Exception) {
                checkoutStatus = SubmitStatus.Error("Gagal Check-Out: ${e.message}")
            }
        }
    }
}