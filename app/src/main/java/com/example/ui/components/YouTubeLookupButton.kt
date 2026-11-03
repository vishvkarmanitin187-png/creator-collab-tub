package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.SubscriberRange
import com.example.ui.theme.*
import com.example.youtube.YouTubeApi
import com.example.youtube.YouTubeChannel
import kotlinx.coroutines.launch

private fun formatCount(n: Long): String = when {
    n >= 1_000_000 -> String.format("%.1fM", n / 1_000_000.0)
    n >= 1_000 -> String.format("%.1fK", n / 1_000.0)
    else -> n.toString()
}

@Composable
fun YouTubeLookupButton(
    handle: String,
    currentLanguage: AppLanguage,
    onFound: (YouTubeChannel, SubscriberRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    val hindi = currentLanguage == AppLanguage.HINDI

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = {
                loading = true
                message = null
                scope.launch {
                    val result = YouTubeApi.findByHandle(context, handle)
                    loading = false
                    result.onSuccess { ch ->
                        onFound(ch, YouTubeApi.pickRange(ch.subscriberCount))
                        isError = false
                        message = if (hindi) {
                            "मिल गया: ${ch.title} (${formatCount(ch.subscriberCount)} subscribers). अब Save करें।"
                        } else {
                            "Found: ${ch.title} (${formatCount(ch.subscriberCount)} subscribers). Now tap Save."
                        }
                    }.onFailure { e ->
                        isError = true
                        message = e.message ?: "Error"
                    }
                }
            },
            enabled = !loading && handle.isNotBlank(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
            border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = ElectricCyan
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hindi) "YouTube से मेरा चैनल भरें" else "Fill from my YouTube channel",
                style = MaterialTheme.typography.labelMedium
            )
        }

        val msg = message
        if (msg != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isError) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isError) RoseError else EmeraldSuccess,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isError) RoseError else EmeraldSuccess
                )
            }
        }
    }
}