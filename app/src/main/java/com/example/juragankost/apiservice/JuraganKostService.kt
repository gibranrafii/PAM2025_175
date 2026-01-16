package com.example.juragankost.apiservice

import com.example.juragankost.modeldata.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface JuraganKostService {

    // --- AUTH ---
    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String
    ): GeneralResponse

    // --- KOST ---
    @GET("get_kost.php")
    suspend fun getKost(
        @Query("id_user") idUser: String
    ): KostResponse

    @FormUrlEncoded
    @POST("add_kost.php")
    suspend fun addKost(
        @Field("id_user") idUser: String,
        @Field("nama_kost") namaKost: String,
        @Field("alamat") alamat: String
    ): GeneralResponse

    // --- KAMAR ---
    @GET("get_kamar.php")
    suspend fun getKamar(
        @Query("id_kost") idKost: String
    ): KamarResponse

    @FormUrlEncoded
    @POST("add_kamar.php")
    suspend fun addKamar(
        @Field("id_kost") idKost: String,
        @Field("nomor_kamar") nomorKamar: String,
        @Field("harga") harga: String
    ): GeneralResponse

    // --- PENGHUNI & CHECK-IN (Multipart) ---
    @Multipart
    @POST("checkin.php")
    suspend fun checkIn(
        @Part("id_room") idRoom: RequestBody,
        @Part("nama_lengkap") namaLengkap: RequestBody,
        @Part("no_hp") noHp: RequestBody,
        @Part fotoKtp: MultipartBody.Part // File Gambar
    ): GeneralResponse

    @GET("get_penghuni.php")
    suspend fun getPenghuni(
        @Query("id_room") idRoom: String
    ): PenghuniResponse

    @FormUrlEncoded
    @POST("checkout.php")
    suspend fun checkOut(
        @Field("id_room") idRoom: String
    ): GeneralResponse

    // --- PAYMENT ---
    @GET("get_payment.php")
    suspend fun getPayment(
        @Query("id_penghuni") idPenghuni: String
    ): PaymentResponse

    @FormUrlEncoded
    @POST("add_payment.php")
    suspend fun addPayment(
        @Field("id_penghuni") idPenghuni: String,
        @Field("nominal") nominal: String,
        @Field("keterangan") keterangan: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("delete_kost.php")
    suspend fun deleteKost(@Field("id_kost") idKost: String): GeneralResponse

    @FormUrlEncoded
    @POST("delete_kamar.php")
    suspend fun deleteKamar(@Field("id_room") idRoom: String): GeneralResponse

    @GET("get_riwayat_kost.php")
    suspend fun getRiwayatKost(@Query("id_kost") idKost: String): PaymentResponse


    // -- UPDATE --
    @FormUrlEncoded
    @POST("update_kost.php")
    suspend fun updateKost(
        @Field("id_kost") idKost: String,
        @Field("nama_kost") namaKost: String,
        @Field("alamat") alamat: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("update_kamar.php")
    suspend fun updateKamar(
        @Field("id_room") idRoom: String,
        @Field("nomor_kamar") nomorKamar: String,
        @Field("harga") harga: String
    ): GeneralResponse

    @Multipart
    @POST("update_penghuni.php")
    suspend fun updatePenghuni(
        @Part("id_penghuni") idPenghuni: RequestBody,
        @Part("nama") nama: RequestBody,
        @Part("hp") hp: RequestBody,
        // Nullable (?) artinya foto boleh dikirim, boleh tidak
        @Part foto: MultipartBody.Part? = null
    ): GeneralResponse

    @GET("get_penghuni_by_id.php")
    suspend fun getPenghuniById(@Query("id_penghuni") id: String): PenghuniResponse

    @GET("get_kost_by_id.php")
    suspend fun getKostById(@Query("id_kost") id: String): SingleKostResponse

    @GET("get_kamar_by_id.php")
    suspend fun getKamarById(@Query("id_room") id: String): SingleKamarResponse
}