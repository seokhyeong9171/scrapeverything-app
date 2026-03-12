package com.scrapeverything.app.ui.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrapeverything.app.data.local.TokenStorage
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    tokenStorage: TokenStorage,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    // 자동 로그인 판단
    LaunchedEffect(Unit) {
        delay(800) // 스플래시 최소 표시 시간

        if (tokenStorage.isKeepLoggedIn() && tokenStorage.hasTokens()) {
            onNavigateToMain()
        } else {
            tokenStorage.clearTokens()
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "조각모음",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "흩어진 스크랩을 한곳에",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
