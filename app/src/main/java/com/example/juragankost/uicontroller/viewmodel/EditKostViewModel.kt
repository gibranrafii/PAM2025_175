package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.repositori.JuraganKostRepository
import kotlinx.coroutines.launch

class EditKostViewModel(private val repository: JuraganKostRepository) : ViewModel() {

    // State form, mirip entry tapi kita tidak butuh isEditMode lagi karena ini KHUSUS Edit
    var namaKost by mutableStateOf("")
        private set
    var alamat by mutableStateOf("")
        private set

    var submitStatus by mutableStateOf<SubmitStatus>(SubmitStatus.Idle)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun updateState(nama: String? = null, alamatBaru: String? = null) {
        if (nama != null) namaKost = nama
        if (alamatBaru != null) alamat = alamatBaru
    }

    // Load Data Awal (Supaya form terisi data lama)
    fun loadKost(idKost: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getKostById(idKost)
                if (response.success && response.data != null) {
                    namaKost = response.data.namaKost
                    alamat = response.data.alamat
                }
            } catch (e: Exception) {
                // Handle error load
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi Update (Simpan Perubahan)
    fun updateKost(idKost: String) {
        viewModelScope.launch {
            submitStatus = SubmitStatus.Loading
            try {
                // Panggil repository Update
                val response = repository.updateKost(idKost, namaKost, alamat)
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