package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class CheckInUiState(
    val nama: String = "",
    val noHp: String = "",
    val fotoUri: String? = null, // Menyimpan alamat foto di HP sementara
    val submitStatus: SubmitStatus = SubmitStatus.Idle
)

class CheckInViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var uiState by mutableStateOf(CheckInUiState())
        private set

    fun updateUiState(nama: String = uiState.nama, noHp: String = uiState.noHp, fotoUri: String? = uiState.fotoUri) {
        uiState = uiState.copy(nama = nama, noHp = noHp, fotoUri = fotoUri)
    }

    fun checkIn(idRoom: String, fileFoto: File) {
        viewModelScope.launch {
            uiState = uiState.copy(submitStatus = SubmitStatus.Loading)
            try {
                // 1. Siapkan Data Teks
                val idRoomBody = idRoom.toRequestBody("text/plain".toMediaTypeOrNull())
                val namaBody = uiState.nama.toRequestBody("text/plain".toMediaTypeOrNull())
                val hpBody = uiState.noHp.toRequestBody("text/plain".toMediaTypeOrNull())

                // 2. Siapkan Data Gambar (Multipart)
                val requestFile = fileFoto.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("foto_ktp", fileFoto.name, requestFile)

                // 3. Kirim ke Repo
                val response = repository.checkIn(idRoomBody, namaBody, hpBody, multipartBody)

                if (response.success) {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Success)
                } else {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Error(response.message))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(submitStatus = SubmitStatus.Error("Gagal: ${e.message}"))
            }
        }
    }
}