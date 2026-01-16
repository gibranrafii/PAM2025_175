package com.example.juragankost.repositori

import com.example.juragankost.apiservice.JuraganKostService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface AppContainer {
    val juraganKostRepository: JuraganKostRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "http://10.0.2.2/JuraganKostAPI/"

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(jsonConfig.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .client(client)
        .build()

    private val retrofitService: JuraganKostService by lazy {
        retrofit.create(JuraganKostService::class.java)
    }

    override val juraganKostRepository: JuraganKostRepository by lazy {
        NetworkJuraganKostRepository(retrofitService)
    }
}