package com.scrapeverything.app.ui.navigation

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object Register : Route("register")
    object CategoryList : Route("categories")
    object ScrapList : Route("categories/{categoryId}/scraps?categoryName={categoryName}") {
        fun createRoute(categoryId: Long, categoryName: String): String =
            "categories/$categoryId/scraps?categoryName=$categoryName"
    }
    object ScrapDetail : Route("scraps/{scrapId}") {
        fun createRoute(scrapId: Long): String = "scraps/$scrapId"
    }
    object ScrapEdit : Route("scraps/{scrapId}/edit") {
        fun createRoute(scrapId: Long): String = "scraps/$scrapId/edit"
    }
    object MyPage : Route("mypage")
}
