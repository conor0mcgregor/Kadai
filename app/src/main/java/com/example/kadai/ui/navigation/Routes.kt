package com.example.kadai.ui.navigation

/**
 * Rutas de navegación centralizadas.
 * Solo se definen los destinos que ya tienen pantalla implementada.
 */
object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val GROUPS = "groups"
    const val CREATE_GROUP = "create_group"
    const val JOIN_GROUP = "join_group"
    const val INVITE = "invite/{groupId}"
    const val GROUP_DETAIL = "group_detail/{groupId}"
    const val MANAGE_MEMBERS = "manage_members/{groupId}"
    const val RANKING = "ranking/{groupId}"
    const val CHALLENGE_DETAIL = "challenge_detail/{dailyChallengeId}"
    const val CHALLENGE_TODAY = "challenge_today"
    const val SUBMIT_PROOF = "submit_proof"
    const val GROUP_PROOFS = "group_proofs/{groupId}"
    const val PROOF_DETAIL = "proof_detail/{proofId}?groupId={groupId}"
    const val CHALLENGE_HISTORY = "challenge_history/{groupId}"
    const val PROPOSE_CHALLENGE = "propose_challenge"

    fun groupDetail(groupId: String) = "group_detail/$groupId"
    fun manageMembers(groupId: String) = "manage_members/$groupId"
    fun ranking(groupId: String) = "ranking/$groupId"
    fun invite(groupId: String) = "invite/$groupId"
    fun challengeDetail(dailyChallengeId: String) = "challenge_detail/$dailyChallengeId"
    fun groupProofs(groupId: String) = "group_proofs/$groupId"
    fun proofDetail(proofId: String, groupId: String) =
        if (groupId.isBlank()) "proof_detail/$proofId"
        else "proof_detail/$proofId?groupId=$groupId"
    fun challengeHistory(groupId: String) = "challenge_history/$groupId"
}
