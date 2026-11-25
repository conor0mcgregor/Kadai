package com.example.kadai.ui.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Fábrica sencilla para ViewModels que necesitan argumentos de navegación.
 * Evita duplicar una clase `Factory` por pantalla.
 */
class KadaiViewModelFactory<VM : ViewModel>(
    private val application: Application,
    private val create: (Application) -> VM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = create(application) as T
}
