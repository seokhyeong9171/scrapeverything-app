package com.scrapeverything.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.network.SessionManager
import com.scrapeverything.app.ui.auth.LoginScreen
import com.scrapeverything.app.ui.auth.RegisterScreen
import com.scrapeverything.app.ui.category.CategoryListScreen
import com.scrapeverything.app.ui.member.MyPageScreen
import com.scrapeverything.app.ui.scrap.ScrapDetailScreen
import com.scrapeverything.app.ui.scrap.ScrapEditScreen
import com.scrapeverything.app.ui.scrap.ScrapListScreen
import com.scrapeverything.app.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    tokenStorage: TokenStorage,
    sessionManager: SessionManager
) {
    // 세션 만료 시 로그인 화면으로 이동
    LaunchedEffect(Unit) {
        sessionManager.sessionExpiredEvent.collect {
            navController.navigate(Route.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        // 스플래시
        composable(Route.Splash.route) {
            SplashScreen(
                tokenStorage = tokenStorage,
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Route.CategoryList.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 로그인
        composable(Route.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.CategoryList.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
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
                onNavigateBack = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 마이페이지
        composable(Route.MyPage.route) {
            MyPageScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.CategoryList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
