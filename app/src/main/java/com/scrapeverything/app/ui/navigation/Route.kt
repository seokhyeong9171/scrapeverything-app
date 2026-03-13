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
    object ScrapAdd : Route("categories/{categoryId}/scraps/add") {
        fun createRoute(categoryId: Long): String = "categories/$categoryId/scraps/add"
    }
    object ScrapEdit : Route("scraps/{scrapId}/edit") {
        fun createRoute(scrapId: Long): String = "scraps/$scrapId/edit"
    }
    object ScrapAddFromShare : Route("scraps/add/share?sharedUrl={sharedUrl}") {
        fun createRoute(sharedUrl: String): String =
            "scraps/add/share?sharedUrl=${java.net.URLEncoder.encode(sharedUrl, "UTF-8")}"
    }
    object MyPage : Route("mypage")
}
