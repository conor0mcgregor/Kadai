package com.example.kadai.data.mock

import com.example.kadai.domain.model.Challenge
import com.example.kadai.domain.model.ChallengeStatus
import com.example.kadai.domain.model.DailyChallenge
import com.example.kadai.domain.model.DailyChallengeStatus
import com.example.kadai.domain.model.Favorite
import com.example.kadai.domain.model.Group
import com.example.kadai.domain.model.GroupStatus
import com.example.kadai.domain.model.GroupValidation
import com.example.kadai.domain.model.MediaType
import com.example.kadai.domain.model.MemberRole
import com.example.kadai.domain.model.Membership
import com.example.kadai.domain.model.MembershipStatus
import com.example.kadai.domain.model.Notification
import com.example.kadai.domain.model.NotificationType
import com.example.kadai.domain.model.Proof
import com.example.kadai.domain.model.Reaction
import com.example.kadai.domain.model.ReactionType
import com.example.kadai.domain.model.Score
import com.example.kadai.domain.model.User
import com.example.kadai.domain.model.ValidationStatus
import com.example.kadai.domain.model.Vote

object MockData {

    // ── Usuarios ──────────────────────────────────────────────────────────────

    val currentUserId = "user_alex"

    val users = listOf(
        User(uid = "user_alex",   username = "Alex García",  email = "alex@kadai.app",   streak = 7,  createdAt = 1700000000L),
        User(uid = "user_maria",  username = "María R.",     email = "maria@kadai.app",  streak = 5,  createdAt = 1700000100L),
        User(uid = "user_carlos", username = "Carlos M.",    email = "carlos@kadai.app", streak = 12, createdAt = 1700000200L),
        User(uid = "user_sara",   username = "Sara L.",      email = "sara@kadai.app",   streak = 3,  createdAt = 1700000300L),
        User(uid = "user_jorge",  username = "Jorge P.",     email = "jorge@kadai.app",  streak = 1,  createdAt = 1700000400L),
    )

    // ── Grupos ────────────────────────────────────────────────────────────────

    val groups = listOf(
        Group(groupId = "group_pandilla",   name = "Pandilla",    description = "El grupo de siempre",         photoUrl = "res://group_cover_pandilla", adminId = "user_alex",   status = GroupStatus.ACTIVE, createdAt = 1700100000L, inviteCode = "PANDIL"),
        Group(groupId = "group_uni",        name = "Uni 2024",    description = "Compañeros de universidad",   photoUrl = "res://group_cover_uni",      adminId = "user_carlos", status = GroupStatus.ACTIVE, createdAt = 1700100100L, inviteCode = "UNI202"),
        Group(groupId = "group_curro",      name = "Curro Squad", description = "Los del trabajo",             photoUrl = "res://group_cover_curro",    adminId = "user_jorge",  status = GroupStatus.ACTIVE, createdAt = 1700100200L, inviteCode = "CURROS"),
        Group(groupId = "group_musicos",    name = "Músicos",     description = "Para los amantes de la música", photoUrl = "res://group_cover_musicos", adminId = "user_sara",   status = GroupStatus.ACTIVE, createdAt = 1700100300L, inviteCode = "MUSICO"),
    )

    // ── Membresías ────────────────────────────────────────────────────────────

    val memberships = listOf(
        Membership(groupId = "group_pandilla", userId = "user_alex",   role = MemberRole.ADMIN,  status = MembershipStatus.ACTIVE, joinedAt = 1700100000L),
        Membership(groupId = "group_pandilla", userId = "user_maria",  role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100050L),
        Membership(groupId = "group_pandilla", userId = "user_carlos", role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100060L),
        Membership(groupId = "group_pandilla", userId = "user_sara",   role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100070L),
        Membership(groupId = "group_pandilla", userId = "user_jorge",  role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100080L),
        Membership(groupId = "group_uni",      userId = "user_alex",   role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100100L),
        Membership(groupId = "group_uni",      userId = "user_carlos", role = MemberRole.ADMIN,  status = MembershipStatus.ACTIVE, joinedAt = 1700100100L),
        Membership(groupId = "group_uni",      userId = "user_maria",  role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100110L),
        Membership(groupId = "group_curro",    userId = "user_alex",   role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100200L),
        Membership(groupId = "group_curro",    userId = "user_jorge",  role = MemberRole.ADMIN,  status = MembershipStatus.ACTIVE, joinedAt = 1700100200L),
        Membership(groupId = "group_musicos",  userId = "user_sara",   role = MemberRole.ADMIN,  status = MembershipStatus.ACTIVE, joinedAt = 1700100300L),
        Membership(groupId = "group_musicos",  userId = "user_alex",   role = MemberRole.MEMBER, status = MembershipStatus.ACTIVE, joinedAt = 1700100310L),
    )

    // ── Retos ─────────────────────────────────────────────────────────────────

    val challenges = listOf(
        Challenge(challengeId = "ch_001", title = "Grita en medio de la calle", description = "Sube un vídeo gritando en medio de la calle, cuando antes lo hagas, más puntos te llevas", category = "Atrevido", createdBy = "admin", status = ChallengeStatus.APPROVED, createdAt = 1699900000L),
        Challenge(challengeId = "ch_002", title = "Desayuno saludable", description = "Fotografía tu desayuno más saludable del mes", category = "Lifestyle", createdBy = "admin", status = ChallengeStatus.APPROVED, createdAt = 1699900100L),
        Challenge(challengeId = "ch_003", title = "Selfie al amanecer", description = "Despiértate a tiempo y haz una foto al amanecer", category = "Naturaleza", createdBy = "admin", status = ChallengeStatus.APPROVED, createdAt = 1699900200L),
    )

    val activeChallenge = DailyChallenge(
        dailyChallengeId = "daily_001",
        challengeId      = "ch_001",
        publishedAt      = System.currentTimeMillis() - 3_600_000L,
        closesAt         = System.currentTimeMillis() + 29_600_000L,
        status           = DailyChallengeStatus.OPEN
    )

    val challengeHistory = listOf(
        DailyChallenge(dailyChallengeId = "daily_prev1", challengeId = "ch_002", publishedAt = 1700300000L, closesAt = 1700386400L, status = DailyChallengeStatus.CLOSED),
        DailyChallenge(dailyChallengeId = "daily_prev2", challengeId = "ch_003", publishedAt = 1700213600L, closesAt = 1700300000L, status = DailyChallengeStatus.CLOSED),
    )

    // ── Pruebas ───────────────────────────────────────────────────────────────

    val proofs = listOf(
        Proof(proofId = "proof_001", dailyChallengeId = "daily_001", userId = "user_maria",  mediaType = MediaType.VIDEO, storagePath = "proofs/daily_001/user_maria.mp4",  submittedAt = System.currentTimeMillis() - 840_000L,   updatedAt = System.currentTimeMillis() - 840_000L),
        Proof(proofId = "proof_002", dailyChallengeId = "daily_001", userId = "user_carlos", mediaType = MediaType.VIDEO, storagePath = "proofs/daily_001/user_carlos.mp4", submittedAt = System.currentTimeMillis() - 3_600_000L, updatedAt = System.currentTimeMillis() - 3_600_000L),
        Proof(proofId = "proof_003", dailyChallengeId = "daily_001", userId = "user_sara",   mediaType = MediaType.PHOTO, storagePath = "proofs/daily_001/user_sara.jpg",   submittedAt = System.currentTimeMillis() - 7_200_000L, updatedAt = System.currentTimeMillis() - 7_200_000L),
    )

    // ── Validaciones por grupo ────────────────────────────────────────────────

    val groupValidations = listOf(
        GroupValidation(proofId = "proof_001", groupId = "group_pandilla", status = ValidationStatus.PENDING, positiveVotes = 8, negativeVotes = 1, isValid = false),
        GroupValidation(proofId = "proof_002", groupId = "group_uni",      status = ValidationStatus.PENDING, positiveVotes = 2, negativeVotes = 0, isValid = false),
    )

    // ── Votos ─────────────────────────────────────────────────────────────────

    val votes = listOf(
        Vote(voteId = "vote_001", proofId = "proof_001", groupId = "group_pandilla", userId = "user_alex",   isPositive = true,  createdAt = System.currentTimeMillis() - 600_000L, updatedAt = System.currentTimeMillis() - 600_000L),
        Vote(voteId = "vote_002", proofId = "proof_001", groupId = "group_pandilla", userId = "user_carlos", isPositive = true,  createdAt = System.currentTimeMillis() - 500_000L, updatedAt = System.currentTimeMillis() - 500_000L),
        Vote(voteId = "vote_003", proofId = "proof_001", groupId = "group_pandilla", userId = "user_jorge",  isPositive = false, createdAt = System.currentTimeMillis() - 400_000L, updatedAt = System.currentTimeMillis() - 400_000L),
        Vote(voteId = "vote_004", proofId = "proof_002", groupId = "group_uni",      userId = "user_alex",   isPositive = true,  createdAt = System.currentTimeMillis() - 700_000L, updatedAt = System.currentTimeMillis() - 700_000L),
        Vote(voteId = "vote_005", proofId = "proof_002", groupId = "group_uni",      userId = "user_maria",  isPositive = true,  createdAt = System.currentTimeMillis() - 650_000L, updatedAt = System.currentTimeMillis() - 650_000L),
        Vote(voteId = "vote_006", proofId = "proof_003", groupId = "group_pandilla", userId = "user_alex",   isPositive = true,  createdAt = System.currentTimeMillis() - 300_000L, updatedAt = System.currentTimeMillis() - 300_000L),
        Vote(voteId = "vote_007", proofId = "proof_003", groupId = "group_pandilla", userId = "user_maria",  isPositive = false, createdAt = System.currentTimeMillis() - 250_000L, updatedAt = System.currentTimeMillis() - 250_000L),
    )

    // ── Puntuaciones ──────────────────────────────────────────────────────────

    val scores = listOf(
        Score(groupId = "group_pandilla", userId = "user_alex",   dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 50, timePoints = 70, totalPoints = 220, calculatedAt = 1700386400L),
        Score(groupId = "group_pandilla", userId = "user_maria",  dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 40, timePoints = 60, totalPoints = 200, calculatedAt = 1700386400L),
        Score(groupId = "group_pandilla", userId = "user_carlos", dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 30, timePoints = 50, totalPoints = 180, calculatedAt = 1700386400L),
        Score(groupId = "group_pandilla", userId = "user_sara",   dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 20, timePoints = 40, totalPoints = 160, calculatedAt = 1700386400L),
        Score(groupId = "group_pandilla", userId = "user_jorge",  dailyChallengeId = "daily_prev1", completionPoints = 0,   positionPoints = 0,  timePoints = 0,  totalPoints = 0,   calculatedAt = 1700386400L),
        Score(groupId = "group_pandilla", userId = "user_alex",   dailyChallengeId = "daily_prev2", completionPoints = 100, positionPoints = 50, timePoints = 70, totalPoints = 220, calculatedAt = 1700300000L),
        Score(groupId = "group_pandilla", userId = "user_carlos", dailyChallengeId = "daily_prev2", completionPoints = 100, positionPoints = 40, timePoints = 60, totalPoints = 200, calculatedAt = 1700300000L),
        Score(groupId = "group_uni",      userId = "user_carlos", dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 20, timePoints = 45, totalPoints = 165, calculatedAt = 1700386400L),
        Score(groupId = "group_uni",      userId = "user_alex",   dailyChallengeId = "daily_prev1", completionPoints = 100, positionPoints = 10, timePoints = 35, totalPoints = 145, calculatedAt = 1700386400L),
        Score(groupId = "group_uni",      userId = "user_maria",  dailyChallengeId = "daily_prev1", completionPoints = 0,   positionPoints = 0,  timePoints = 0,  totalPoints = 0,   calculatedAt = 1700386400L),
        Score(groupId = "group_musicos",  userId = "user_sara",   dailyChallengeId = "daily_prev2", completionPoints = 100, positionPoints = 10, timePoints = 25, totalPoints = 135, calculatedAt = 1700300000L),
        Score(groupId = "group_musicos",  userId = "user_alex",   dailyChallengeId = "daily_prev2", completionPoints = 100, positionPoints = 0,  timePoints = 15, totalPoints = 115, calculatedAt = 1700300000L),
    )

    // ── Reacciones ────────────────────────────────────────────────────────────

    val reactions = listOf(
        Reaction(reactionId = "react_001", proofId = "proof_001", userId = "user_sara",   type = ReactionType.FIRE,   createdAt = System.currentTimeMillis() - 300_000L),
        Reaction(reactionId = "react_002", proofId = "proof_001", userId = "user_carlos", type = ReactionType.CLAP,   createdAt = System.currentTimeMillis() - 200_000L),
        Reaction(reactionId = "react_003", proofId = "proof_001", userId = "user_jorge",  type = ReactionType.ROCKET, createdAt = System.currentTimeMillis() - 100_000L),
    )

    // ── Favoritos ─────────────────────────────────────────────────────────────

    val favorites = listOf(
        Favorite(userId = "user_alex", proofId = "proof_003", savedAt = System.currentTimeMillis() - 100_000L),
    )

    // ── Notificaciones ────────────────────────────────────────────────────────

    val notifications = listOf(
        Notification(notificationId = "notif_001", userId = "user_alex", type = NotificationType.NEW_CHALLENGE,    title = "¡Nuevo reto del día!",          body = "Grita en medio de la calle. ¡Atrévete!",        relatedId = "daily_001",  createdAt = System.currentTimeMillis() - 3_600_000L),
        Notification(notificationId = "notif_002", userId = "user_alex", type = NotificationType.PROOF_VOTED,      title = "María valoró tu prueba",        body = "María R. ha votado positivamente tu prueba.",   relatedId = "proof_001",  createdAt = System.currentTimeMillis() - 1_800_000L),
        Notification(notificationId = "notif_003", userId = "user_alex", type = NotificationType.REACTION_RECEIVED, title = "Sara reaccionó a tu prueba",   body = "Sara L. reaccionó con 🔥 a tu prueba.",         relatedId = "proof_001",  createdAt = System.currentTimeMillis() - 900_000L,  readAt = System.currentTimeMillis() - 600_000L),
    )
}
