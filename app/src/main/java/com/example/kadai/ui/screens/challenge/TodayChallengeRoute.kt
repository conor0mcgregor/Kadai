package com.example.kadai.ui.screens.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kadai.ui.components.KadaiErrorState
import com.example.kadai.ui.components.KadaiLoadingState
import com.example.kadai.ui.theme.Background

@Composable
fun TodayChallengeRoute(
    onOpenChallenge: (dailyChallengeId: String) -> Unit,
    viewModel: TodayChallengeViewModel = viewModel()
) {
    val dailyChallengeId by viewModel.dailyChallengeId.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(dailyChallengeId) {
        dailyChallengeId?.let(onOpenChallenge)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        when {
            error != null -> KadaiErrorState(message = error.orEmpty(), onRetry = viewModel::retry)
            else -> KadaiLoadingState(message = "Buscando el reto de hoy…")
        }
    }
}
