package com.example.registrocriminal.database

import android.app.Application

class RegistroCriminalApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CrimenRepository.inicializar(this)
    }
}
