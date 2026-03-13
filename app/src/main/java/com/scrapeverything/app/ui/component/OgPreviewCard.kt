package com.scrapeverything.app.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun OgPreviewCard(
    ogTitle: String?,
    ogDescription: String?,
    ogImageUrl: String?,
    modifier: Modifier = Modifier
) {
    if (ogTitle == null && ogDescription == null && ogImageUrl == null) return

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            if (!ogImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = ogImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                if (!ogTitle.isNullOrBlank()) {
                    Text(
                        text = ogTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!ogDescription.isNullOrBlank()) {
                    if (!ogTitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = ogDescription,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
