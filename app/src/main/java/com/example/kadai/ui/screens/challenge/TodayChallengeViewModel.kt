package com.example.kadai.ui.screens.challenge

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kadai.ui.base.KadaiViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Resuelve el reto activo para que la barra inferior pueda abrirlo sin conocer su id.
 */
class TodayChallengeViewModel(application: Application) : KadaiViewModel(application) {

    private val challengeRepository = container.challengeRepository

    private val _dailyChallengeId = MutableStateFlow<String?>(null)
    val dailyChallengeId = _dailyChallengeId.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        viewModelScope.launch {
            runCatching {
                challengeRepository.getActiveChallenge()
                    ?: error("Hoy no hay ningún reto activo.")
            }.onSuccess { daily ->
                _dailyChallengeId.value = daily.dailyChallengeId
            }.onFailure { error ->
                _error.value = error.message ?: "No se pudo abrir el reto del día."
            }
        }
    }
}
