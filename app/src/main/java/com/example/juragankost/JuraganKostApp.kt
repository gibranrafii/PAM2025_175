package com.example.juragankost

import android.app.Application
import com.example.juragankost.repositori.AppContainer
import com.example.juragankost.repositori.DefaultAppContainer

class JuraganKostApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}