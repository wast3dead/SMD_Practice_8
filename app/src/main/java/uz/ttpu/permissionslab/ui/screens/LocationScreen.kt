package uz.ttpu.permissionslab.ui.screens

/*
 * =========================================================================================
 * TASK 5: One-Time & Location Permission (Fine vs Coarse)
 * -----------------------------------------------------------------------------------------
 * 1. Requests ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION via RequestMultiplePermissions.
 * 2. Parses resulting Map<String, Boolean> to show Precise, Approximate, or Denied states.
 * 3. Crucially re-verifies permission state inside an ON_RESUME lifecycle handler to properly
 *    handle "Only this time" one-time permissions or background revocations.
 * =========================================================================================
 */

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

enum class LocationPermissionStatus {
    PRECISE,      // Fine location granted
    APPROXIMATE,  // Coarse location granted (fine denied)
    DENIED        // Neither granted
}

@Composable
fun LocationScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var locationStatus by remember { mutableStateOf(checkLocationPermissionState(context)) }

    // Re-verify location permission on ON_RESUME handler
    // This is crucial for handling "Only this time" (one-time) permissions
    // which expire when the app is backgrounded or when revoked by system.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        locationStatus = checkLocationPermissionState(context)
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val fineGranted = permissionsMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissionsMap[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        locationStatus = when {
            fineGranted -> LocationPermissionStatus.PRECISE
            coarseGranted -> LocationPermissionStatus.APPROXIMATE
            else -> LocationPermissionStatus.DENIED
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Task 5: Location Permission",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Fine, Coarse & One-Time Handling",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (locationStatus) {
                    LocationPermissionStatus.PRECISE -> MaterialTheme.colorScheme.primaryContainer
                    LocationPermissionStatus.APPROXIMATE -> MaterialTheme.colorScheme.tertiaryContainer
                    LocationPermissionStatus.DENIED -> MaterialTheme.colorScheme.errorContainer
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val (badgeBg, badgeText, statusTitle, statusDesc) = when (locationStatus) {
                    LocationPermissionStatus.PRECISE -> Quadruple(
                        Color(0xFF2E7D32),
                        "🎯",
                        "Precise Location Granted",
                        "App has full access to high-accuracy GPS / Fine Location."
                    )
                    LocationPermissionStatus.APPROXIMATE -> Quadruple(
                        Color(0xFFE65100),
                        "📍",
                        "Approximate Location Granted",
                        "App has access to network-based Coarse Location (~3km accuracy)."
                    )
                    LocationPermissionStatus.DENIED -> Quadruple(
                        Color(0xFFC62828),
                        "❌",
                        "Location Access Denied",
                        "No location permission granted. Please grant permission to test."
                    )
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = badgeText, fontSize = 38.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = statusTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = statusDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        multiplePermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Request Location Permission")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "One-Time Permission Handling",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "When users select 'Only this time', Android grants temporary permission. The ON_RESUME handler re-checks status whenever the app regains focus.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun checkLocationPermissionState(context: Context): LocationPermissionStatus {
    val fineGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return when {
        fineGranted -> LocationPermissionStatus.PRECISE
        coarseGranted -> LocationPermissionStatus.APPROXIMATE
        else -> LocationPermissionStatus.DENIED
    }
}

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
