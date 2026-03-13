package com.scrapeverything.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.scrapeverything.app.data.local.ThemeMode
import com.scrapeverything.app.data.local.ThemePreferences
import com.scrapeverything.app.data.local.TokenStorage
import com.scrapeverything.app.network.SessionManager
import com.scrapeverything.app.ui.component.AdBanner
import com.scrapeverything.app.ui.navigation.NavGraph
import com.scrapeverything.app.ui.navigation.Route
import com.scrapeverything.app.ui.theme.ScrapEverythingTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenStorage: TokenStorage

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var themePreferences: ThemePreferences

    private var sharedUrl by mutableStateOf<String?>(null)

    private fun extractSharedUrl(intent: Intent?): String? {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            return intent.getStringExtra(Intent.EXTRA_TEXT)
        }
        return null
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        extractSharedUrl(intent)?.let { url ->
            sharedUrl = url
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        enableEdgeToEdge()

        sharedUrl = extractSharedUrl(intent)

        setContent {
            val themeMode by themePreferences.themeModeFlow.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.AUTO -> systemDark
            }

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                    }
                )
                onDispose {}
            }

            ScrapEverythingTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                        val navController = rememberNavController()
                        val currentSharedUrl = sharedUrl
                        NavGraph(
                            navController = navController,
                            tokenStorage = tokenStorage,
                            sessionManager = sessionManager,
                            sharedUrl = currentSharedUrl,
                            onSharedUrlConsumed = { sharedUrl = null },
                            modifier = Modifier.weight(1f)
                        )
                        AdBanner()
                    }
                }
            }
        }
    }
}
