package com.scrapeverything.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.scrapeverything.app.data.local.SharedUrlHolder
import com.scrapeverything.app.data.local.ThemeMode
import com.scrapeverything.app.data.local.ThemePreferences
import com.scrapeverything.app.ui.component.AdBanner
import com.scrapeverything.app.ui.navigation.NavGraph
import com.scrapeverything.app.ui.theme.ScrapEverythingTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    @Inject
    lateinit var sharedUrlHolder: SharedUrlHolder

    private var sharedUrl by mutableStateOf<String?>(null)

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            // 사용자가 업데이트를 취소하면 다시 강제 업데이트 요청
            checkForUpdate()
        }
    }

    private fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
                // 업데이트 중이었는데 앱이 재시작된 경우 이어서 진행
                appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
        }.addOnFailureListener { e ->
            Log.d("InAppUpdate", "Update check failed: ${e.message}")
        }
    }

    private fun extractAndStoreSharedUrl(intent: Intent?): String? {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
            val url = extractUrlFromText(text)
            if (url != null) {
                sharedUrlHolder.url = url
            }
            return url
        }
        return null
    }

    private fun extractUrlFromText(text: String): String? {
        val urlPattern = Regex("https?://\\S+")
        return urlPattern.find(text)?.value
    }

    override fun onResume() {
        super.onResume()
        // 앱이 포그라운드로 돌아왔을 때 업데이트가 진행 중이면 이어서 처리
        checkForUpdate()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        extractAndStoreSharedUrl(intent)?.let { url ->
            sharedUrl = url
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        enableEdgeToEdge()
        checkForUpdate()

        sharedUrl = extractAndStoreSharedUrl(intent)

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
