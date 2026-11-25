package com.example.kadai.di

import com.example.kadai.data.mock.MockData
import com.example.kadai.data.repository.FakeChallengeRepository
import com.example.kadai.data.repository.FakeGroupRepository
import com.example.kadai.data.repository.FakeNotificationRepository
import com.example.kadai.data.repository.FakeProofRepository
import com.example.kadai.data.repository.FakeRankingRepository
import com.example.kadai.data.repository.FakeUserRepository
import com.example.kadai.domain.repository.ChallengeRepository
import com.example.kadai.domain.repository.GroupRepository
import com.example.kadai.domain.repository.NotificationRepository
import com.example.kadai.domain.repository.ProofRepository
import com.example.kadai.domain.repository.RankingRepository
import com.example.kadai.domain.repository.UserRepository

/**
 * Contenedor manual de dependencias.
 *
 * Centraliza la creación de repositorios. Para pasar de Fake a Firebase,
 * basta con cambiar las implementaciones aquí sin tocar el dominio ni las pantallas.
 */
class AppContainer {
    /**
     * Usuario de la sesión simulada. Cuando se integre Firebase Authentication
     * este valor pasará a proceder del proveedor de autenticación.
     */
    val currentUserId: String = MockData.currentUserId

    val userRepository: UserRepository = FakeUserRepository()
    val groupRepository: GroupRepository = FakeGroupRepository()
    val challengeRepository: ChallengeRepository = FakeChallengeRepository()
    val proofRepository: ProofRepository = FakeProofRepository()
    val rankingRepository: RankingRepository = FakeRankingRepository()
    val notificationRepository: NotificationRepository = FakeNotificationRepository()
}
