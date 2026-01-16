package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch

class EditKamarViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    var nomorKamar by mutableStateOf("")
        private set
    var harga by mutableStateOf("")
        private set

    var submitStatus by mutableStateOf<SubmitStatus>(SubmitStatus.Idle)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun updateState(nomor: String? = null, hargaBaru: String? = null) {
        if (nomor != null) nomorKamar = nomor
        if (hargaBaru != null) harga = hargaBaru
    }

    // Load Data Kamar Lama
    fun loadKamar(idRoom: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getKamarById(idRoom)
                if (response.success && response.data != null) {
                    nomorKamar = response.data.nomorKamar
                    harga = response.data.harga
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    // Simpan Perubahan
    fun updateKamar(idRoom: String) {
        viewModelScope.launch {
            submitStatus = SubmitStatus.Loading
            try {
                // Kita pakai updateKamar yang sudah dibuat di langkah sebelumnya
                val response = repository.updateKamar(idRoom, nomorKamar, harga)
                if (response.success) {
                    submitStatus = SubmitStatus.Success
                } else {
                    submitStatus = SubmitStatus.Error(response.message)
                }
            } catch (e: Exception) {
                submitStatus = SubmitStatus.Error(e.message ?: "Gagal update")
            }
        }
    }
}