package com.example.kadai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kadai.ui.screens.challenge.ChallengeDetailRoute
import com.example.kadai.ui.screens.challenge.ChallengeHistoryRoute
import com.example.kadai.ui.screens.challenge.ProposeChallengeRoute
import com.example.kadai.ui.screens.challenge.SubmitProofRoute
import com.example.kadai.ui.screens.challenge.TodayChallengeRoute
import com.example.kadai.ui.screens.groups.CreateGroupRoute
import com.example.kadai.ui.screens.groups.GroupDetailRoute
import com.example.kadai.ui.screens.groups.GroupsRoute
import com.example.kadai.ui.screens.groups.InviteRoute
import com.example.kadai.ui.screens.groups.JoinGroupRoute
import com.example.kadai.ui.screens.groups.ManageMembersRoute
import com.example.kadai.ui.screens.groups.RankingRoute
import com.example.kadai.ui.screens.home.HomeRoute
import com.example.kadai.ui.screens.login.LoginScreen
import com.example.kadai.ui.screens.proof.GroupProofsRoute
import com.example.kadai.ui.screens.proof.ProofDetailRoute

/**
 * Grafo de navegación centralizado de Kadai.
 *
 * Para agregar nuevas pantallas: añadir el composable() correspondiente aquí
 * y registrar la ruta en Routes.kt. No es necesario modificar MainActivity.
 */
@Composable
fun KadaiNavHost(
    navController: NavHostController,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { _, _ ->
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { /* TODO: registrar ruta cuando exista la pantalla */ },
                onForgotClick   = { /* TODO: registrar ruta cuando exista la pantalla */ }
            )
        }

        composable(Routes.HOME) {
            HomeRoute(
                onGroupClick = { groupId ->
                    navController.navigate(Routes.groupDetail(groupId))
                },
                onChallengeClick = { dailyChallengeId ->
                    navController.navigate(Routes.challengeDetail(dailyChallengeId))
                },
                onGroupsClick = { navController.navigate(Routes.GROUPS) },
                onCreateGroupClick = { navController.navigate(Routes.CREATE_GROUP) }
            )
        }

        composable(Routes.GROUPS) {
            GroupsRoute(
                onGroupClick = { groupId ->
                    navController.navigate(Routes.groupDetail(groupId))
                },
                onCreateGroupClick = { navController.navigate(Routes.CREATE_GROUP) },
                onJoinGroupClick = { navController.navigate(Routes.JOIN_GROUP) }
            )
        }

        composable(Routes.CREATE_GROUP) {
            CreateGroupRoute(
                onBack = { navController.popBackStack() },
                onCreated = { groupId ->
                    navController.navigate(Routes.groupDetail(groupId)) {
                        popUpTo(Routes.GROUPS)
                    }
                }
            )
        }

        composable(Routes.JOIN_GROUP) {
            JoinGroupRoute(
                onBack = { navController.popBackStack() },
                onJoined = { groupId ->
                    navController.navigate(Routes.groupDetail(groupId)) {
                        popUpTo(Routes.GROUPS)
                    }
                }
            )
        }

        composable(Routes.GROUP_DETAIL) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            GroupDetailRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                onManageMembers = { id -> navController.navigate(Routes.manageMembers(id)) },
                onInvite = { id -> navController.navigate(Routes.invite(id)) },
                onRanking = { id -> navController.navigate(Routes.ranking(id)) },
                onHistory = { id -> navController.navigate(Routes.challengeHistory(id)) },
                onGroupProofs = { id -> navController.navigate(Routes.groupProofs(id)) },
                onGroupGone = {
                    navController.navigate(Routes.GROUPS) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.MANAGE_MEMBERS) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            ManageMembersRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INVITE) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            InviteRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.RANKING) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            RankingRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CHALLENGE_TODAY) {
            TodayChallengeRoute(
                onOpenChallenge = { dailyChallengeId ->
                    navController.navigate(Routes.challengeDetail(dailyChallengeId)) {
                        popUpTo(Routes.CHALLENGE_TODAY) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CHALLENGE_DETAIL) { entry ->
            val dailyChallengeId = entry.arguments?.getString("dailyChallengeId").orEmpty()
            ChallengeDetailRoute(
                dailyChallengeId = dailyChallengeId,
                onBack = { navController.popBackStack() },
                onSubmitProof = { navController.navigate(Routes.SUBMIT_PROOF) },
                onViewGroupProofs = { groupId ->
                    navController.navigate(Routes.groupProofs(groupId))
                },
                onProposeChallenge = { navController.navigate(Routes.PROPOSE_CHALLENGE) },
                onViewHistory = { groupId ->
                    navController.navigate(Routes.challengeHistory(groupId))
                },
                onViewMyProof = { proofId ->
                    navController.navigate(Routes.proofDetail(proofId, ""))
                }
            )
        }

        composable(Routes.SUBMIT_PROOF) {
            SubmitProofRoute(
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() }
            )
        }

        composable(Routes.GROUP_PROOFS) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            GroupProofsRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                onOpenProof = { proofGroupId, proofId ->
                    navController.navigate(Routes.proofDetail(proofId, proofGroupId))
                }
            )
        }

        composable(
            route = Routes.PROOF_DETAIL,
            arguments = listOf(
                navArgument("proofId") { type = NavType.StringType },
                navArgument("groupId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val proofId = entry.arguments?.getString("proofId").orEmpty()
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            ProofDetailRoute(
                proofId = proofId,
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CHALLENGE_HISTORY) { entry ->
            val groupId = entry.arguments?.getString("groupId").orEmpty()
            ChallengeHistoryRoute(
                groupId = groupId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROPOSE_CHALLENGE) {
            ProposeChallengeRoute(onBack = { navController.popBackStack() })
        }
    }
}
