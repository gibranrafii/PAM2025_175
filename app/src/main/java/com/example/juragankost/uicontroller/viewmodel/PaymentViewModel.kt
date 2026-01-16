package com.example.juragankost.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juragankost.modeldata.Payment
import com.example.juragankost.repositori.JuraganKostRepository
import com.example.juragankost.uicontroller.route.DestinasiPayment
import kotlinx.coroutines.launch

// State UI
data class PaymentUiState(
    val paymentList: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val isError: String? = null,

    // State untuk Form Input
    val nominalInput: String = "",
    val keteranganInput: String = "",
    val submitStatus: SubmitStatus = SubmitStatus.Idle
)

class PaymentViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: JuraganKostRepository
) : ViewModel() {

    private val idPenghuni: String = checkNotNull(savedStateHandle[DestinasiPayment.idPenghuniArg])

    var uiState by mutableStateOf(PaymentUiState())
        private set

    init {
        getPaymentList()
    }

    fun updateInput(nominal: String = uiState.nominalInput, keterangan: String = uiState.keteranganInput) {
        uiState = uiState.copy(nominalInput = nominal, keteranganInput = keterangan)
    }

    fun getPaymentList() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            try {
                val response = repository.getPayment(idPenghuni)
                if (response.success) {
                    uiState = uiState.copy(paymentList = response.payments, isLoading = false)
                } else {
                    // Jika gagal/kosong, list jadi kosong
                    uiState = uiState.copy(paymentList = emptyList(), isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isError = e.message, isLoading = false)
            }
        }
    }

    fun addPayment() {
        viewModelScope.launch {
            uiState = uiState.copy(submitStatus = SubmitStatus.Loading)
            try {
                val response = repository.addPayment(idPenghuni, uiState.nominalInput, uiState.keteranganInput)
                if (response.success) {
                    uiState = uiState.copy(
                        submitStatus = SubmitStatus.Success,
                        nominalInput = "",     // Reset form
                        keteranganInput = ""
                    )
                    getPaymentList() // Refresh list otomatis setelah tambah
                } else {
                    uiState = uiState.copy(submitStatus = SubmitStatus.Error(response.message))
                }
            } catch (e: Exception) {
                uiState = uiState.copy(submitStatus = SubmitStatus.Error("Gagal: ${e.message}"))
            }
        }
    }
}