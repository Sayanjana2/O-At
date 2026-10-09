package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DesktopApp
import com.example.domain.model.WindowState
import com.example.presentation.ViewMode
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSubtle
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TaskbarGlass
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Taskbar(
    runningWindows: List<WindowState>,
    focusedWindowId: String?,
    isStartMenuOpen: Boolean,
    unreadNotificationCount: Int,
    onToggleStartMenu: () -> Unit,
    onToggleNotifications: () -> Unit,
    onToggleQuickSettings: () -> Unit,
    onAppClick: (DesktopApp) -> Unit,
    onShowDesktop: () -> Unit,
    onSwitchToTouchpad: () -> Unit,
    onSwitchToConnectionHub: () -> Unit,
    onSwitchToDocs: () -> Unit
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("desktop_taskbar"),
        color = TaskbarGlass,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Group: Start Menu + Pinned/Running Apps
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Start Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isStartMenuOpen) CyanAccent else GlassSurfaceElevated)
                        .clickable { onToggleStartMenu() }
                        .testTag("start_menu_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Start",
                        tint = if (isStartMenuOpen) DarkNavyBackground else CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Mode switch shortcuts: Touchpad, Connection Hub, Receiver Guide
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyBackground.copy(alpha = 0.5f))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ModeButton(
                        icon = Icons.Default.TouchApp,
                        label = "Trackpad",
                        onClick = onSwitchToTouchpad,
                        testTag = "nav_trackpad"
                    )
                    ModeButton(
                        icon = Icons.Default.CastConnected,
                        label = "Hub",
                        onClick = onSwitchToConnectionHub,
                        testTag = "nav_hub"
                    )
                    ModeButton(
                        icon = Icons.Default.Description,
                        label = "Docs",
                        onClick = onSwitchToDocs,
                        testTag = "nav_docs"
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Running App Taskbar Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    runningWindows.forEach { win ->
                        val isFocused = win.id == focusedWindowId && !win.isMinimized
                        TaskbarAppItem(
                            windowState = win,
                            isFocused = isFocused,
                            onClick = { onAppClick(win.app) }
                        )
                    }
                }
            }

            // Right Group: System Tray (Wi-Fi, Battery, Vol, Notif, Clock, Peek)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Quick settings trigger box (Wi-Fi, Vol, Battery)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GlassSurfaceElevated)
                        .clickable { onToggleQuickSettings() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("quick_settings_tray"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Battery",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "94%",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Notifications Bell
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GlassSurfaceElevated)
                        .clickable { onToggleNotifications() }
                        .testTag("notification_tray_button"),
                    contentAlignment = Alignment.Center
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationCount > 0) {
                                Badge(
                                    containerColor = CyanAccent,
                                    contentColor = DarkNavyBackground
                                ) {
                                    Text(text = "$unreadNotificationCount", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (unreadNotificationCount > 0) CyanAccent else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Clock & Date Box
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GlassSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = currentTime.ifEmpty { "10:42 AM" },
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentDate.ifEmpty { "Oct 9" },
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Peek Desktop Button (Win+D equivalent)
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GlassBorder)
                        .clickable { onShowDesktop() }
                        .testTag("show_desktop_button")
                )
            }
        }
    }
}

@Composable
private fun ModeButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = CyanAccent,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TaskbarAppItem(
    windowState: WindowState,
    isFocused: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) CyanAccent.copy(alpha = 0.2f) else GlassSurfaceElevated)
            .border(
                1.dp,
                if (isFocused) CyanAccent.copy(alpha = 0.5f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = windowState.app.icon,
                contentDescription = windowState.title,
                tint = if (isFocused) CyanAccent else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = windowState.app.title.split(" ").first(),
                color = if (isFocused) TextPrimary else TextSecondary,
                fontSize = 11.sp,
                fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Normal
            )
        }

        // Active indicator dot under icon
        if (!windowState.isMinimized) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(CyanAccent)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
