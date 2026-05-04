package com.example.shintech

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}
