package com.example.juragankost.uicontroller.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.juragankost.JuraganKostApp
import com.example.juragankost.uicontroller.viewmodel.CheckInViewModel
import com.example.juragankost.uicontroller.viewmodel.DetailKostViewModel
import com.example.juragankost.uicontroller.viewmodel.DetailPenghuniViewModel
import com.example.juragankost.uicontroller.viewmodel.EditKamarViewModel
import com.example.juragankost.uicontroller.viewmodel.EditKostViewModel
import com.example.juragankost.uicontroller.viewmodel.EditPenghuniViewModel
import com.example.juragankost.uicontroller.viewmodel.EntryKamarViewModel
import com.example.juragankost.uicontroller.viewmodel.EntryKostViewModel
import com.example.juragankost.uicontroller.viewmodel.HomeViewModel
import com.example.juragankost.uicontroller.viewmodel.LoginViewModel
import com.example.juragankost.uicontroller.viewmodel.PaymentViewModel
import com.example.juragankost.uicontroller.viewmodel.RegisterViewModel
import com.example.juragankost.uicontroller.viewmodel.RiwayatKostViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // Initializer untuk LoginViewModel
        initializer {
            LoginViewModel(
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // TAMBAHAN: RegisterViewModel
        initializer {
            RegisterViewModel(juraganKostApplication().container.juraganKostRepository)
        }
        //initializer Home
        initializer {
            HomeViewModel(
                this.createSavedStateHandle(), // Ini buat menangkap argumen navigasi
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // TAMBAHAN: Initializer untuk EntryKostViewModel
        initializer {
            EntryKostViewModel(
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // Initializer untuk DetailKost
        initializer {
            DetailKostViewModel(
                this.createSavedStateHandle(),
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // TAMBAHAN: EntryKamarViewModel
        initializer {
            EntryKamarViewModel(
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // TAMBAHAN: CheckInViewModel
        initializer {
            CheckInViewModel(
                juraganKostApplication().container.juraganKostRepository
            )
        }
        // TAMBAHAN: DetailPenghuniViewModel
        initializer {
            DetailPenghuniViewModel(
                this.createSavedStateHandle(),
                juraganKostApplication().container.juraganKostRepository
            )
        }

        initializer {
            PaymentViewModel(
                this.createSavedStateHandle(),
                juraganKostApplication().container.juraganKostRepository
            )
        }
        initializer {
            RiwayatKostViewModel(juraganKostApplication().container.juraganKostRepository)
        }

        initializer {
            EditPenghuniViewModel(juraganKostApplication().container.juraganKostRepository)
        }
        initializer {
            EditKostViewModel(juraganKostApplication().container.juraganKostRepository)
        }
        initializer {
            EditKamarViewModel(juraganKostApplication().container.juraganKostRepository)
        }
    }
}



// Fungsi ekstensi agar kode lebih ringkas
fun CreationExtras.juraganKostApplication(): JuraganKostApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as JuraganKostApp)