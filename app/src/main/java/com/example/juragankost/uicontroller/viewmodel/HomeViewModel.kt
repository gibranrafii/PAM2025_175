package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.Kost
import com.example.juragankost.repositori.JuraganKostRepository
import com.example.juragankost.uicontroller.route.DestinasiHome
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val kostList: List<Kost>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    savedStateHandle: SavedStateHandle, // Untuk menangkap ID User dari Navigasi
    private val repository: JuraganKostRepository
) : ViewModel() {

    // Ambil ID User yang dikirim dari Login
    private val idUser: String = checkNotNull(savedStateHandle[DestinasiHome.idUserArg])

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    init {
        getKostList()
    }

    fun getKostList() {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                // Panggil API getKost
                val response = repository.getKost(idUser)

                if (response.success) {
                    homeUiState = HomeUiState.Success(response.kosts)
                } else {
                    homeUiState = HomeUiState.Error("Data kosong atau gagal muat")
                }
            } catch (e: IOException) {
                homeUiState = HomeUiState.Error("Koneksi gagal. Cek internet/server.")
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error("Error: ${e.message}")
            }
        }
    }
    fun deleteKost(idKost: String) {
        viewModelScope.launch {
            try {
                repository.deleteKost(idKost)
                getKostList() // Refresh data setelah hapus
            } catch (e: Exception) {
                homeUiState = HomeUiState.Error("Gagal hapus: ${e.message}")
            }
        }
    }
}