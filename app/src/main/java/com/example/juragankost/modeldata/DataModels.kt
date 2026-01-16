package com.example.juragankost.modeldata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- AUTH MODELS ---
@Serializable
data class LoginResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String,
    @SerialName("user") val user: User? = null
)

@Serializable
data class User(
    @SerialName("id_user") val idUser: String,
    @SerialName("username") val username: String
)

@Serializable
data class GeneralResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String
)

// --- KOST MODELS ---
@Serializable
data class KostResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("kosts") val kosts: List<Kost> = emptyList()
)

@Serializable
data class Kost(
    @SerialName("id_kost") val idKost: String,
    @SerialName("nama_kost") val namaKost: String,
    val alamat: String,

    @SerialName("jumlah_penghuni") val jumlahPenghuni: String = "0",
    @SerialName("total_kamar") val totalKamar: String = "0"
)

// --- KAMAR MODELS ---
@Serializable
data class KamarResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("kamars") val kamars: List<Kamar> = emptyList()
)

@Serializable
data class Kamar(
    @SerialName("id_room") val idRoom: String,
    @SerialName("id_kost") val idKost: String,
    @SerialName("nomor_kamar") val nomorKamar: String,
    val harga: String,
    @SerialName("is_occupied") val isOccupied: String, // "0" atau "1"

    // TAMBAHAN BARU (Boleh null jika kamar kosong)
    @SerialName("id_penghuni") val idPenghuni: String? = null,
    @SerialName("nama_lengkap") val namaPenghuni: String? = null
)

// --- PENGHUNI MODELS ---
@Serializable
data class PenghuniResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("data") val data: Penghuni? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class Penghuni(
    @SerialName("id_penghuni") val idPenghuni: String,
    @SerialName("id_room") val idRoom: String,
    @SerialName("nama_lengkap") val namaLengkap: String,
    @SerialName("no_hp") val noHp: String,
    @SerialName("foto_ktp_path") val fotoKtpPath: String,
    @SerialName("tanggal_masuk") val tanggalMasuk: String
)

// --- PAYMENT MODELS ---
@Serializable
data class PaymentResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("payments") val payments: List<Payment> = emptyList()
)

@Serializable
data class Payment(
    @SerialName("id_payment") val idPayment: String,
    @SerialName("id_penghuni") val idPenghuni: String,
    @SerialName("tanggal_bayar") val tanggalBayar: String,
    @SerialName("nominal") val nominal: String,
    @SerialName("keterangan") val keterangan: String
)

@Serializable
data class SingleKostResponse(
    val success: Boolean,
    val data: Kost?, // Single object, bukan List
    val message: String = ""
)

@Serializable
data class SingleKamarResponse(
    val success: Boolean,
    val data: Kamar?,
    val message: String = ""
)