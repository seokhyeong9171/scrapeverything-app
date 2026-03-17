package com.scrapeverything.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scrapeverything.app.ui.auth.LoginScreen
import com.scrapeverything.app.ui.auth.RegisterScreen
import com.scrapeverything.app.ui.backup.BackupRestoreScreen
import com.scrapeverything.app.ui.category.CategoryListScreen
import com.scrapeverything.app.ui.member.MyPageScreen
import com.scrapeverything.app.ui.notice.NoticeScreen
import com.scrapeverything.app.ui.scrap.ScrapAddFromShareScreen
import com.scrapeverything.app.ui.scrap.ScrapAddScreen
import com.scrapeverything.app.ui.scrap.ScrapDetailScreen
import com.scrapeverything.app.ui.scrap.ScrapEditScreen
import com.scrapeverything.app.ui.scrap.ScrapListScreen
import com.scrapeverything.app.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    sharedUrl: String? = null,
    onSharedUrlConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 앱이 이미 실행 중일 때 외부 공유 처리 (onNewIntent)
    LaunchedEffect(sharedUrl) {
        if (!sharedUrl.isNullOrBlank()) {
            val currentRoute = navController.currentDestination?.route
            if (currentRoute != null && currentRoute != Route.Splash.route) {
                navController.navigate(Route.ScrapAddFromShare.route)
                onSharedUrlConsumed()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(150)) },
        exitTransition = { fadeOut(animationSpec = tween(150)) },
        popEnterTransition = { fadeIn(animationSpec = tween(150)) },
        popExitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        // 스플래시
        composable(Route.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Route.CategoryList.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                    if (!sharedUrl.isNullOrBlank()) {
                        navController.navigate(Route.ScrapAddFromShare.route)
                        onSharedUrlConsumed()
                    }
                }
            )
        }

        // 로그인
        composable(Route.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(Route.Register.route)
                }
            )
        }

        // 회원가입
        composable(Route.Register.route) {
            RegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // 메인 - 카테고리 목록
        composable(Route.CategoryList.route) {
            CategoryListScreen(
                onNavigateToScrapList = { categoryId, categoryName ->
                    navController.navigate(Route.ScrapList.createRoute(categoryId, categoryName))
                },
                onNavigateToMyPage = {
                    navController.navigate(Route.MyPage.route)
                }
            )
        }

        // 스크랩 목록
        composable(
            route = Route.ScrapList.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("categoryName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            ScrapListScreen(
                onNavigateToScrapDetail = { scrapId ->
                    navController.navigate(Route.ScrapDetail.createRoute(scrapId))
                },
                onNavigateToScrapAdd = { categoryId ->
                    navController.navigate(Route.ScrapAdd.createRoute(categoryId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 스크랩 추가
        composable(
            route = Route.ScrapAdd.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType }
            )
        ) {
            ScrapAddScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // 스크랩 상세
        composable(
            route = Route.ScrapDetail.route,
            arguments = listOf(
                navArgument("scrapId") { type = NavType.LongType }
            )
        ) {
            ScrapDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onDeleteSuccess = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { scrapId ->
                    navController.navigate(Route.ScrapEdit.createRoute(scrapId))
                }
            )
        }

        // 스크랩 수정
        composable(
            route = Route.ScrapEdit.route,
            arguments = listOf(
                navArgument("scrapId") { type = NavType.LongType }
            )
        ) {
            ScrapEditScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // 외부 공유로 스크랩 추가
        composable(route = Route.ScrapAddFromShare.route) {
            ScrapAddFromShareScreen(
                onNavigateBack = { navController.popBackStack() },
                onSaveSuccess = { categoryId, categoryName ->
                    navController.navigate(Route.ScrapList.createRoute(categoryId, categoryName)) {
                        popUpTo(Route.CategoryList.route) { inclusive = false }
                    }
                }
            )
        }

        // 마이페이지
        composable(Route.MyPage.route) {
            MyPageScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route)
                },
                onNavigateToBackupRestore = {
                    navController.navigate(Route.BackupRestore.route)
                },
                onNavigateToNotice = {
                    navController.navigate(Route.Notice.route)
                }
            )
        }

        // 백업/복원
        composable(Route.BackupRestore.route) {
            BackupRestoreScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 공지사항
        composable(Route.Notice.route) {
            NoticeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
