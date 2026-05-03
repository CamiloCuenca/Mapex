package com.mapex.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import kotlinx.coroutines.delay

@Suppress("unused")
@Composable
fun ConnectionStatusIndicator(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(isNetworkAvailable(context)) }
    var hasBeenOffline by remember { mutableStateOf(!isConnected) }
    var showReconnected by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if (!isConnected) {
                    isConnected = true
                    if (hasBeenOffline) {
                        showReconnected = true
                    }
                }
            }

            override fun onLost(network: Network) {
                isConnected = false
                hasBeenOffline = true
                showReconnected = false
            }

            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                if (!isConnected) {
                    isConnected = true
                    if (hasBeenOffline) {
                        showReconnected = true
                    }
                }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    LaunchedEffect(showReconnected) {
        if (showReconnected) {
            delay(3000) // Ocultar después de 3 segundos
            showReconnected = false
        }
    }

    AnimatedVisibility(
        visible = !isConnected || showReconnected,
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut(),
        modifier = modifier
    ) {
        val isOffline = !isConnected
        val color = if (isOffline) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
        val textStr = if (isOffline) "Modo Offline" else "Reconectado"
        val icon = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone

        OutlinedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            ),
            border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = textStr,
                    modifier = Modifier.size(18.dp),
                    tint = color
                )
                Text(
                    text = textStr,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    letterSpacing = 0.05.em
                )
            }
        }
    }
}

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
