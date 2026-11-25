package com.example.kadai.app

import android.app.Application
import com.example.kadai.di.AppContainer

class KadaiApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
