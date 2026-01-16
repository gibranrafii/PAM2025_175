package com.example.juragankost.repositori

import com.example.juragankost.apiservice.JuraganKostService
import com.example.juragankost.modeldata.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

interface JuraganKostRepository {
    suspend fun login(username: String, password: String): LoginResponse
    suspend fun register(username: String, password: String): GeneralResponse
    suspend fun getKost(idUser: String): KostResponse
    suspend fun addKost(idUser: String, namaKost: String, alamat: String): GeneralResponse
    suspend fun getKamar(idKost: String): KamarResponse
    suspend fun addKamar(idKost: String, nomor: String, harga: String): GeneralResponse
    suspend fun checkIn(idRoom: RequestBody, nama: RequestBody, hp: RequestBody, foto: MultipartBody.Part): GeneralResponse
    suspend fun getPenghuni(idRoom: String): PenghuniResponse
    suspend fun checkOut(idRoom: String): GeneralResponse
    suspend fun getPayment(idPenghuni: String): PaymentResponse
    suspend fun addPayment(idPenghuni: String, nominal: String, keterangan: String): GeneralResponse
    suspend fun deleteKost(idKost: String): GeneralResponse
    suspend fun deleteKamar(idRoom: String): GeneralResponse
    suspend fun getRiwayatKost(idKost: String): PaymentResponse

    suspend fun updateKost(idKost: String, nama: String, alamat: String): GeneralResponse
    suspend fun updateKamar(idRoom: String, nomor: String, harga: String): GeneralResponse
    suspend fun updatePenghuni(idPenghuni: String, nama: String, hp: String, foto: File?): GeneralResponse
    suspend fun getPenghuniById(id: String): PenghuniResponse
    suspend fun getKostById(id: String): SingleKostResponse
    suspend fun getKamarById(id: String): SingleKamarResponse
}

class NetworkJuraganKostRepository(
    private val juraganKostService: JuraganKostService
) : JuraganKostRepository {

    override suspend fun login(username: String, password: String) = juraganKostService.login(username, password)
    override suspend fun register(username: String, password: String) = juraganKostService.register(username, password)

    override suspend fun getKost(idUser: String) = juraganKostService.getKost(idUser)
    override suspend fun addKost(idUser: String, namaKost: String, alamat: String) = juraganKostService.addKost(idUser, namaKost, alamat)

    override suspend fun getKamar(idKost: String) = juraganKostService.getKamar(idKost)
    override suspend fun addKamar(idKost: String, nomor: String, harga: String) = juraganKostService.addKamar(idKost, nomor, harga)

    override suspend fun checkIn(idRoom: RequestBody, nama: RequestBody, hp: RequestBody, foto: MultipartBody.Part) = juraganKostService.checkIn(idRoom, nama, hp, foto)
    override suspend fun getPenghuni(idRoom: String) = juraganKostService.getPenghuni(idRoom)
    override suspend fun checkOut(idRoom: String) = juraganKostService.checkOut(idRoom)

    override suspend fun getPayment(idPenghuni: String) = juraganKostService.getPayment(idPenghuni)
    override suspend fun addPayment(idPenghuni: String, nominal: String, keterangan: String) = juraganKostService.addPayment(idPenghuni, nominal, keterangan)

    override suspend fun deleteKost(idKost: String) = juraganKostService.deleteKost(idKost)

    override suspend fun deleteKamar(idRoom: String) = juraganKostService.deleteKamar(idRoom)
    override suspend fun getRiwayatKost(idKost: String) = juraganKostService.getRiwayatKost(idKost)
    override suspend fun updateKost(idKost: String, nama: String, alamat: String) =
        juraganKostService.updateKost(idKost, nama, alamat)

    override suspend fun updateKamar(idRoom: String, nomor: String, harga: String) =
        juraganKostService.updateKamar(idRoom, nomor, harga)

    override suspend fun updatePenghuni(idPenghuni: String, nama: String, hp: String, foto: File?): GeneralResponse {
        // Konversi String ke RequestBody
        val idPart = idPenghuni.toRequestBody("text/plain".toMediaTypeOrNull())
        val namaPart = nama.toRequestBody("text/plain".toMediaTypeOrNull())
        val hpPart = hp.toRequestBody("text/plain".toMediaTypeOrNull())

        // Konversi File ke MultipartBody.Part (Hanya jika foto ada)
        val fotoPart = if (foto != null) {
            val requestFile = foto.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("foto", foto.name, requestFile)
        } else {
            null // Kirim null jika user tidak ganti foto
        }

        return juraganKostService.updatePenghuni(idPart, namaPart, hpPart, fotoPart)
    }
    override suspend fun getPenghuniById(id: String) = juraganKostService.getPenghuniById(id)
    override suspend fun getKostById(id: String) = juraganKostService.getKostById(id)
    override suspend fun getKamarById(id: String) = juraganKostService.getKamarById(id)
}