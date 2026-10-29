package uz.ttpu.permissionslab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uz.ttpu.permissionslab.ui.screens.CameraScreen
import uz.ttpu.permissionslab.ui.screens.LocationScreen
import uz.ttpu.permissionslab.ui.screens.NetworkScreen
import uz.ttpu.permissionslab.ui.screens.NotificationScreen
import uz.ttpu.permissionslab.ui.screens.OverlayScreen
import uz.ttpu.permissionslab.ui.theme.PermissionsLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PermissionsLabTheme {
                MainAppScreen()
            }
        }
    }
}

enum class LabTab(val title: String, val icon: String) {
    NETWORK("Task 2: Network", "🌐"),
    CAMERA("Task 3: Camera", "📷"),
    OVERLAY("Task 4: Overlay", "🪟"),
    LOCATION("Task 5: Location", "📍"),
    NOTIFICATION("Task 6: Notify", "🔔")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = LabTab.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Permissions Lab",
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = "${tab.icon} ${tab.title}",
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            val screenModifier = Modifier.weight(1f)
            when (tabs[selectedTabIndex]) {
                LabTab.NETWORK -> NetworkScreen(modifier = screenModifier)
                LabTab.CAMERA -> CameraScreen(modifier = screenModifier)
                LabTab.OVERLAY -> OverlayScreen(modifier = screenModifier)
                LabTab.LOCATION -> LocationScreen(modifier = screenModifier)
                LabTab.NOTIFICATION -> NotificationScreen(modifier = screenModifier)
            }
        }
    }
}
