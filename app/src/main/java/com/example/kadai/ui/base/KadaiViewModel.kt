package com.example.kadai.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.kadai.app.KadaiApplication
import com.example.kadai.di.AppContainer

/**
 * Base de los ViewModels del proyecto.
 *
 * Centraliza el acceso al contenedor de dependencias y a la sesión simulada,
 * de modo que los ViewModels no dependan de `MockData` ni de implementaciones
 * concretas. Con Firebase solo cambiará el contenedor.
 */
abstract class KadaiViewModel(application: Application) : AndroidViewModel(application) {

    protected val container: AppContainer
        get() = (getApplication<Application>() as KadaiApplication).container

    protected val currentUserId: String
        get() = container.currentUserId
}
