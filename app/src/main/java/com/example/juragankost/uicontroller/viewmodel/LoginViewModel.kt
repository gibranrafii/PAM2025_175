package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.User
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch
import java.io.IOException

// Status Login (Loading, Sukses, Gagal)
sealed interface LoginStatus {
    object Idle : LoginStatus
    object Loading : LoginStatus
    object Success : LoginStatus
    data class Error(val message: String) : LoginStatus
}

// State untuk UI (Apa yang tampil di layar)
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val loginStatus: LoginStatus = LoginStatus.Idle,
    val loggedInUser: User? = null // Menyimpan data user yang berhasil login
)

class LoginViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    // Fungsi untuk update ketikan user di TextField
    fun updateUiState(username: String = uiState.username, password: String = uiState.password) {
        uiState = uiState.copy(username = username, password = password)
    }

    // Fungsi Login yang dipanggil saat tombol diklik
    fun login() {
        viewModelScope.launch {
            // Set status jadi Loading
            uiState = uiState.copy(loginStatus = LoginStatus.Loading)

            try {
                // Panggil Repository
                val response = repository.login(uiState.username, uiState.password)

                if (response.success) {
                    uiState = uiState.copy(
                        loginStatus = LoginStatus.Success,
                        loggedInUser = response.user
                    )
                } else {
                    uiState = uiState.copy(
                        loginStatus = LoginStatus.Error(response.message)
                    )
                }
            } catch (e: IOException) {
                uiState = uiState.copy(loginStatus = LoginStatus.Error("Kesalahan Jaringan: Cek koneksi internet/IP"))
            } catch (e: Exception) {
                uiState = uiState.copy(loginStatus = LoginStatus.Error("Terjadi Kesalahan: ${e.message}"))
            }
        }
    }

    // Fungsi Reset status (misal setelah error, mau login lagi)
    fun resetLoginStatus() {
        uiState = uiState.copy(loginStatus = LoginStatus.Idle)
    }
}