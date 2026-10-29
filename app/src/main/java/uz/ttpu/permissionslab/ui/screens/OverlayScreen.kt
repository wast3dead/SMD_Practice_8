package uz.ttpu.permissionslab.ui.screens

/*
 * =========================================================================================
 * TASK 4: Special Permission (Overlay / SYSTEM_ALERT_WINDOW)
 * -----------------------------------------------------------------------------------------
 * 1. Special permissions cannot be requested via normal runtime permission dialogs.
 * 2. If Settings.canDrawOverlays(context) returns false, toggling the Switch launches
 *    Settings.ACTION_MANAGE_OVERLAY_PERMISSION with package URI "package:${context.packageName}".
 * 3. Uses LifecycleEventEffect(Lifecycle.Event.ON_RESUME) to re-check Settings.canDrawOverlays(context)
 *    every time the app resumes from system settings, automatically updating the Switch state dynamically.
 * =========================================================================================
 */

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

@Composable
fun OverlayScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Local state tracking whether overlay permission is currently granted
    var canDrawOverlays by remember { mutableStateOf(Settings.canDrawOverlays(context)) }

    // Re-check overlay permission whenever the app resumes from system settings
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        canDrawOverlays = Settings.canDrawOverlays(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Task 4: Special Permission",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "SYSTEM_ALERT_WINDOW (Overlay)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Display over other apps",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (canDrawOverlays) "Permission Granted" else "Permission Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (canDrawOverlays) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }

                    // Switch toggling state / launching settings
                    Switch(
                        checked = canDrawOverlays,
                        onCheckedChange = { isChecked ->
                            if (!canDrawOverlays && isChecked) {
                                // Missing permission: launch overlay management settings
                                openOverlaySettings(context)
                            } else if (!isChecked) {
                                // If already granted and user flips switch off, prompt settings to revoke
                                openOverlaySettings(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2E7D32)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (canDrawOverlays)
                        "Status: Active. The app can display windows on top of other applications."
                    else
                        "Status: Disabled. Toggle the switch above to grant overlay permission in System Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dynamic ON_RESUME Refresh",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This screen uses LifecycleEventEffect(Lifecycle.Event.ON_RESUME) to check Settings.canDrawOverlays() every time you return from system settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

private fun openOverlaySettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
