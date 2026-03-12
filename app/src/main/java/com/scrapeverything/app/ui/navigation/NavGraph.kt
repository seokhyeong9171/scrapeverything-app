package com.scrapeverything.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.ui.auth.LoginScreen
import com.scrapeverything.app.ui.category.CategoryListScreen
import com.scrapeverything.app.ui.member.MyPageScreen
import com.scrapeverything.app.ui.scrap.ScrapDetailScreen
import com.scrapeverything.app.ui.scrap.ScrapListScreen
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
