package com.example.juragankost.uicontroller.route

import com.example.juragankost.R

interface DestinasiNavigasi {
    val route: String
    val titleRes: String
}

//Register
object DestinasiRegister : DestinasiNavigasi {
    override val route = "register"
    override val titleRes = "Daftar Akun"
}

// Halaman Login
object DestinasiLogin : DestinasiNavigasi {
    override val route = "login"
    override val titleRes = "Login"
}

// Halaman Home (Dashboard)
object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Daftar Kost"
    const val idUserArg = "id_user"
    // Rute lengkap dengan argumen: "home/{id_user}"
    val routeWithArg = "$route/{$idUserArg}"
}

// Tambah Kost
object DestinasiEntryKost : DestinasiNavigasi {
    override val route = "entry_kost"
    override val titleRes = "Tambah Kost Baru"
}

// Detail Kost
object DestinasiDetailKost : DestinasiNavigasi {
    override val route = "detail_kost"
    override val titleRes = "Dashboard Kamar"
    const val idKostArg = "id_kost"
    val routeWithArg = "$route/{$idKostArg}"
}

// Tambahkan Object Baru: Entry Kamar
object DestinasiEntryKamar : DestinasiNavigasi {
    override val route = "entry_kamar"
    override val titleRes = "Tambah Kamar"
    const val idKostArg = "id_kost"
    val routeWithArg = "$route/{$idKostArg}"
}

// Tambahkan Rute Check-In
object DestinasiCheckIn : DestinasiNavigasi {
    override val route = "checkin"
    override val titleRes = "Check In Penghuni"
    const val idRoomArg = "id_room"
    val routeWithArg = "$route/{$idRoomArg}"
}

// Tambahkan Rute Detail Penghuni
object DestinasiDetailPenghuni : DestinasiNavigasi {
    override val route = "detail_penghuni"
    override val titleRes = "Detail Penghuni"
    const val idRoomArg = "id_room"
    val routeWithArg = "$route/{$idRoomArg}"
}

//Payment
object DestinasiPayment : DestinasiNavigasi {
    override val route = "payment"
    override val titleRes = "Riwayat Pembayaran"
    const val idPenghuniArg = "id_penghuni"
    val routeWithArg = "$route/{$idPenghuniArg}"
}

// Riwayat
object DestinasiRiwayatKost : DestinasiNavigasi {
    override val route = "riwayat_kost"

    // PERBAIKAN: Gunakan String langsung, jangan R.string
    override val titleRes = "Riwayat Kost"

    const val idKostArg = "idKost"
    val routeWithArgs = "$route/{$idKostArg}"

}

object DestinasiEditPenghuni : DestinasiNavigasi {
    override val route = "edit_penghuni"
    override val titleRes = "Edit Penghuni"
    const val idPenghuniArg = "id_penghuni"
    val routeWithArgs = "$route/{$idPenghuniArg}"
}

object DestinasiEditKost : DestinasiNavigasi {
    override val route = "edit_kost"
    override val titleRes = "Edit Kost"
    const val idKostArg = "id_kost"
    val routeWithArgs = "$route/{$idKostArg}"
}

object DestinasiEditKamar : DestinasiNavigasi {
    override val route = "edit_kamar"
    override val titleRes = "Edit Kamar"
    const val idRoomArg = "id_room"
    val routeWithArgs = "$route/{$idRoomArg}"
}