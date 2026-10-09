package com.example.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.domain.model.DesktopApp
import com.example.domain.model.SnapState
import com.example.domain.model.StreamTelemetry
import com.example.domain.model.WindowState
import com.example.presentation.DesktopViewModel
import com.example.presentation.apps.BrowserApp
import com.example.presentation.apps.FileManagerApp
import com.example.presentation.apps.MediaPlayerApp
import com.example.presentation.apps.NotesApp
import com.example.presentation.apps.SettingsApp
import com.example.presentation.apps.SystemMonitorApp
import com.example.presentation.apps.TerminalApp
import com.example.presentation.components.NotificationCenter
import com.example.presentation.components.QuickSettingsTray
import com.example.presentation.components.StartMenu
import com.example.presentation.components.Taskbar
import com.example.presentation.components.WindowFrame
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun DesktopScreen(
    viewModel: DesktopViewModel,
    windows: List<WindowState>,
    focusedWindowId: String?,
    isStartMenuOpen: Boolean,
    isNotificationPanelOpen: Boolean,
    isQuickSettingsOpen: Boolean,
    telemetry: StreamTelemetry,
    onSwitchToTouchpad: () -> Unit,
    onSwitchToConnectionHub: () -> Unit,
    onSwitchToDocs: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .testTag("desktop_canvas")
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F1B38),
                        Color(0xFF070B17),
                        Color(0xFF04060E)
                    ),
                    radius = 1800f
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                viewModel.closeAllFlyouts()
            }
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val screenWidthDp = maxWidth.value
        val screenHeightDp = maxHeight.value

        // Desktop Shortcuts column (left)
        Column(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp)
                .zIndex(0.5f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DesktopShortcut(app = DesktopApp.FILES, onClick = { viewModel.openApp(DesktopApp.FILES) })
            DesktopShortcut(app = DesktopApp.MONITOR, onClick = { viewModel.openApp(DesktopApp.MONITOR) })
            DesktopShortcut(app = DesktopApp.TERMINAL, onClick = { viewModel.openApp(DesktopApp.TERMINAL) })
            DesktopShortcut(app = DesktopApp.NOTES, onClick = { viewModel.openApp(DesktopApp.NOTES) })
            DesktopShortcut(app = DesktopApp.BROWSER, onClick = { viewModel.openApp(DesktopApp.BROWSER) })
            DesktopShortcut(app = DesktopApp.MEDIA, onClick = { viewModel.openApp(DesktopApp.MEDIA) })
            DesktopShortcut(app = DesktopApp.SETTINGS, onClick = { viewModel.openApp(DesktopApp.SETTINGS) })
        }

        // Active Application Windows
        windows.forEach { win ->
            val isFocused = win.id == focusedWindowId
            WindowFrame(
                windowState = win,
                isFocused = isFocused,
                screenWidth = screenWidthDp,
                screenHeight = screenHeightDp,
                onFocus = { viewModel.focusWindow(win.id) },
                onClose = { viewModel.closeWindow(win.id) },
                onMinimize = { viewModel.minimizeWindow(win.id) },
                onToggleMaximize = { viewModel.toggleMaximizeWindow(win.id, screenWidthDp, screenHeightDp) },
                onSnapLeft = { viewModel.snapWindow(win.id, SnapState.LEFT_HALF, screenWidthDp, screenHeightDp) },
                onSnapRight = { viewModel.snapWindow(win.id, SnapState.RIGHT_HALF, screenWidthDp, screenHeightDp) },
                onMove = { dx, dy -> viewModel.moveWindow(win.id, dx, dy) },
                onResize = { dw, dh -> viewModel.resizeWindow(win.id, dw, dh) }
            ) {
                when (win.app) {
                    DesktopApp.FILES -> {
                        FileManagerApp(
                            files = viewModel.files.value,
                            onToggleStage = { viewModel.toggleFileStaging(it) }
                        )
                    }
                    DesktopApp.NOTES -> {
                        NotesApp(
                            text = viewModel.notesText.value,
                            onTextChange = { viewModel.updateNotesContent(it) }
                        )
                    }
                    DesktopApp.TERMINAL -> {
                        TerminalApp(
                            lines = viewModel.terminalLines.value,
                            onExecuteCommand = { viewModel.executeTerminalCommand(it) }
                        )
                    }
                    DesktopApp.MONITOR -> {
                        SystemMonitorApp(telemetry = telemetry)
                    }
                    DesktopApp.BROWSER -> {
                        BrowserApp(
                            url = viewModel.browserUrl.value,
                            onUrlChange = { viewModel.setBrowserUrl(it) }
                        )
                    }
                    DesktopApp.MEDIA -> {
                        MediaPlayerApp()
                    }
                    DesktopApp.SETTINGS -> {
                        SettingsApp(
                            settings = viewModel.settings.value,
                            onSettingsChange = { viewModel.updateSettings(it) }
                        )
                    }
                }
            }
        }

        // Start Menu Popup
        AnimatedVisibility(
            visible = isStartMenuOpen,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 10.dp, y = (-60).dp)
                .zIndex(99f)
        ) {
            StartMenu(
                onAppClick = { viewModel.openApp(it) },
                onOpenTouchpad = onSwitchToTouchpad,
                onOpenSettings = { viewModel.openApp(DesktopApp.SETTINGS) },
                onDisconnect = { viewModel.closeAllFlyouts() }
            )
        }

        // Notification Center Popup
        AnimatedVisibility(
            visible = isNotificationPanelOpen,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-10).dp, y = (-60).dp)
                .zIndex(99f)
        ) {
            NotificationCenter(
                notifications = viewModel.notifications.value,
                onDismissNotification = { viewModel.dismissNotification(it) },
                onClearAll = { viewModel.clearNotifications() }
            )
        }

        // Quick Settings Popup
        AnimatedVisibility(
            visible = isQuickSettingsOpen,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-10).dp, y = (-60).dp)
                .zIndex(99f)
        ) {
            QuickSettingsTray(telemetry = telemetry)
        }

        // Bottom Taskbar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .zIndex(90f)
        ) {
            Taskbar(
                runningWindows = windows,
                focusedWindowId = focusedWindowId,
                isStartMenuOpen = isStartMenuOpen,
                unreadNotificationCount = viewModel.notifications.value.size,
                onToggleStartMenu = { viewModel.toggleStartMenu() },
                onToggleNotifications = { viewModel.toggleNotificationPanel() },
                onToggleQuickSettings = { viewModel.toggleQuickSettings() },
                onAppClick = { viewModel.openApp(it) },
                onShowDesktop = { viewModel.minimizeAllWindows() },
                onSwitchToTouchpad = onSwitchToTouchpad,
                onSwitchToConnectionHub = onSwitchToConnectionHub,
                onSwitchToDocs = onSwitchToDocs
            )
        }
    }
}

@Composable
private fun DesktopShortcut(
    app: DesktopApp,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp)
            .testTag("desktop_icon_${app.id}")
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x33101B36))
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = app.icon,
                contentDescription = app.title,
                tint = CyanAccent,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = app.title.split(" ").first(),
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
