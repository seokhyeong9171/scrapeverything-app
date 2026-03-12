package com.scrapeverything.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.ui.auth.LoginScreen
import com.scrapeverything.app.ui.category.CategoryListScreen
import com.scrapeverything.app.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    tokenStorage: TokenStorage
) {
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
                    // 향후 회원가입 화면 구현 시 연결
                }
            )
        }

        // 메인 - 카테고리 목록
        composable(Route.CategoryList.route) {
            CategoryListScreen(
                onNavigateToScrapList = { categoryId, categoryName ->
                    // 향후 스크랩 목록 화면 구현 시 연결
                },
                onNavigateToMyPage = {
                    // 향후 마이페이지 화면 구현 시 연결
                }
            )
        }
    }
}
