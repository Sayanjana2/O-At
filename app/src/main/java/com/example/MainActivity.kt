package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.DesktopRepository
import com.example.presentation.DesktopViewModel
import com.example.presentation.ViewMode
import com.example.presentation.components.OnboardingDialog
import com.example.presentation.screens.ConnectionHubView
import com.example.presentation.screens.DesktopScreen
import com.example.presentation.screens.PhoneTouchpadView
import com.example.presentation.screens.ReceiverDocsView
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DeskifyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = DesktopRepository(applicationContext)

        setContent {
            DeskifyTheme {
                val viewModel: DesktopViewModel = viewModel(
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                            return DesktopViewModel(repository) as T
                        }
                    }
                )

                DeskifyAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DeskifyAppRoot(viewModel: DesktopViewModel) {
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val windows by viewModel.windows.collectAsStateWithLifecycle()
    val focusedWindowId by viewModel.focusedWindowId.collectAsStateWithLifecycle()
    val isStartMenuOpen by viewModel.isStartMenuOpen.collectAsStateWithLifecycle()
    val isNotificationPanelOpen by viewModel.isNotificationPanelOpen.collectAsStateWithLifecycle()
    val isQuickSettingsOpen by viewModel.isQuickSettingsOpen.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val activeReceiver by viewModel.activeReceiver.collectAsStateWithLifecycle()
    val availableReceivers by viewModel.availableReceivers.collectAsStateWithLifecycle()
    val pairingCode by viewModel.wifiPairingCode.collectAsStateWithLifecycle()

    var showOnboarding by remember { mutableStateOf(false) }

    // System BackHandler
    BackHandler(enabled = viewMode != ViewMode.DESKTOP || isStartMenuOpen || isNotificationPanelOpen || isQuickSettingsOpen) {
        if (isStartMenuOpen || isNotificationPanelOpen || isQuickSettingsOpen) {
            viewModel.closeAllFlyouts()
        } else if (viewMode != ViewMode.DESKTOP) {
            viewModel.setViewMode(ViewMode.DESKTOP)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .safeDrawingPadding()
    ) {
        when (viewMode) {
            ViewMode.DESKTOP -> {
                DesktopScreen(
                    viewModel = viewModel,
                    windows = windows,
                    focusedWindowId = focusedWindowId,
                    isStartMenuOpen = isStartMenuOpen,
                    isNotificationPanelOpen = isNotificationPanelOpen,
                    isQuickSettingsOpen = isQuickSettingsOpen,
                    telemetry = telemetry,
                    onSwitchToTouchpad = { viewModel.setViewMode(ViewMode.PHONE_TOUCHPAD) },
                    onSwitchToConnectionHub = { viewModel.setViewMode(ViewMode.CONNECTION_HUB) },
                    onSwitchToDocs = { viewModel.setViewMode(ViewMode.RECEIVER_DOCS) }
                )
            }

            ViewMode.PHONE_TOUCHPAD -> {
                PhoneTouchpadView(
                    onBackToDesktop = { viewModel.setViewMode(ViewMode.DESKTOP) }
                )
            }

            ViewMode.CONNECTION_HUB -> {
                ConnectionHubView(
                    activeReceiver = activeReceiver,
                    availableReceivers = availableReceivers,
                    pairingCode = pairingCode,
                    onRegenerateCode = { viewModel.regeneratePairingCode() },
                    onConnectReceiver = {
                        viewModel.connectReceiver(it)
                        viewModel.setViewMode(ViewMode.DESKTOP)
                    },
                    onBackToDesktop = { viewModel.setViewMode(ViewMode.DESKTOP) }
                )
            }

            ViewMode.RECEIVER_DOCS -> {
                ReceiverDocsView(
                    onBackToDesktop = { viewModel.setViewMode(ViewMode.DESKTOP) }
                )
            }

            ViewMode.ONBOARDING -> {
                // Handled via Dialog
            }
        }

        // Onboarding Dialog
        if (showOnboarding) {
            OnboardingDialog(onDismiss = { showOnboarding = false })
        }
    }
}
