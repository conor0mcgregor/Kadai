package com.example.kadai.ui.screens.challenge

import com.example.kadai.domain.model.MediaType

data class SubmitProofUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val dailyChallengeId: String = "",
    val challengeTitle: String = "",
    val isOpen: Boolean = false,
    val mediaType: MediaType = MediaType.PHOTO,
    val pickedLocation: String? = null,
    val pickedName: String? = null,
    val isReplacing: Boolean = false,
    val validationError: String? = null,
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitted: Boolean = false
)
