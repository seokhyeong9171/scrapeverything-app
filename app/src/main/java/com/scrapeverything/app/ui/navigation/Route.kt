package com.scrapeverything.app.ui.navigation

sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Login : Route("login")
    object CategoryList : Route("categories")
}
