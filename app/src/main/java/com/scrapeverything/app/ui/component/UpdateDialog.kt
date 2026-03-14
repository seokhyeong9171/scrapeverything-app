package com.scrapeverything.app.ui.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun UpdateDialog(
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "업데이트 안내") },
        text = { Text(text = "새로운 버전이 출시되었습니다.\n업데이트하시겠습니까?") },
        confirmButton = {
            TextButton(onClick = onUpdate) {
                Text(
                    text = "업데이트",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "나중에")
            }
        }
    )
}
