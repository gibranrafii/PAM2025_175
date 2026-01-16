package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch
import java.io.File

data class EditPenghuniUiState(
    val nama: String = "",
    val noHp: String = "",
    val fotoLama: String = "", // Menyimpan nama file foto lama dari server
    val fotoBaruUri: String? = null, // Menyimpan URI foto baru jika user ganti foto
    val submitStatus: SubmitStatus = SubmitStatus.Idle,
    val isLoading: Boolean = false
)

class EditPenghuniViewModel(private val repository: JuraganKostRepository) : ViewModel() {
    var uiState by mutableStateOf(EditPenghuniUiState())
        private set

    // 1. Fungsi Load Data Awal (Dipanggil saat halaman dibuka)
    fun loadPenghuni(idPenghuni: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                // PERBAIKAN: Gunakan fungsi getPenghuniById (bukan getPenghuni kamar)
                val response = repository.getPenghuniById(idPenghuni)

                uiState = uiState.copy(
                    nama = response.data?.namaLengkap ?: "",
                    noHp = response.data?.noHp ?: "",
                    fotoLama = response.data?.fotoKtpPath ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    // 2. Update Form State
    fun updateUiState(nama: String? = null, noHp: String? = null, fotoBaruUri: String? = null) {
        uiState = uiState.copy(
            nama = nama ?: uiState.nama,
            noHp = noHp ?: uiState.noHp,
            fotoBaruUri = fotoBaruUri ?: uiState.fotoBaruUri
        )
    }

    // 3. Simpan Perubahan
    fun saveChanges(idPenghuni: String, fotoFile: File?) {
        viewModelScope.launch {
            uiState = uiState.copy(submitStatus = SubmitStatus.Loading)
            try {
                // Panggil Repository Update (Multipart)
                // FotoFile dikirim NULL jika user tidak ganti foto
                val response = repository.updatePenghuni(
                    idPenghuni = idPenghuni,
                    nama = uiState.nama,
                    hp = uiState.noHp,
                    foto = fotoFile
                )

                if (response.success) {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Success)
                } else {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Error(response.message))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(submitStatus = SubmitStatus.Error(e.message ?: "Gagal update"))
            }
        }
    }
}