package com.example.presentation.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.DesktopSettings
import com.example.domain.model.DisplayResolution
import com.example.domain.model.VideoCodec
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsApp(
    settings: DesktopSettings,
    onSettingsChange: (DesktopSettings) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_app")
    ) {
        // Section: Display & Video Pipeline
        SettingsSectionHeader(title = "Display & Hardware Video Stream", icon = Icons.Default.DisplaySettings)
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "Desktop Resolution", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DisplayResolution.values().forEach { res ->
                        val isSelected = settings.resolution == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else DarkNavyBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanAccent else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onSettingsChange(settings.copy(resolution = res)) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (res == DisplayResolution.RES_1080P) "1080p" else if (res == DisplayResolution.RES_1440P) "1440p" else if (res == DisplayResolution.RES_4K) "4K" else "720p",
                                color = if (isSelected) CyanAccent else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Video Codec", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    VideoCodec.values().forEach { codec ->
                        val isSelected = settings.videoCodec == codec
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else DarkNavyBackground)
                                .border(
                                    1.dp,
                                    if (isSelected) CyanAccent else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { onSettingsChange(settings.copy(videoCodec = codec)) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = codec.name,
                                color = if (isSelected) CyanAccent else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Target Bitrate", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "${settings.bitrateMbps} Mbps", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = settings.bitrateMbps.toFloat(),
                    onValueChange = { onSettingsChange(settings.copy(bitrateMbps = it.toInt())) },
                    valueRange = 8f..40f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanAccent,
                        activeTrackColor = CyanAccent,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.testTag("slider_bitrate")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: System & Input Behavior
        SettingsSectionHeader(title = "Desktop Behavior & Input", icon = Icons.Default.Tune)
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SettingsToggleRow(
                    title = "Independent Phone Screen",
                    subtitle = "Phone screen remains independently usable for calls/apps while desktop streams",
                    checked = settings.phoneStaysUsable,
                    onCheckedChange = { onSettingsChange(settings.copy(phoneStaysUsable = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Bi-directional Clipboard Sync",
                    subtitle = "Sync Ctrl+C and Ctrl+V text and screenshots between phone and PC",
                    checked = settings.clipboardSyncEnabled,
                    onCheckedChange = { onSettingsChange(settings.copy(clipboardSyncEnabled = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Auto-launch on USB Connect",
                    subtitle = "Automatically open Deskify desktop when USB receiver handshake succeeds",
                    checked = settings.autoLaunchOnUsb,
                    onCheckedChange = { onSettingsChange(settings.copy(autoLaunchOnUsb = it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Security & Zero Cloud
        SettingsSectionHeader(title = "Security & Zero Cloud Guarantee", icon = Icons.Default.Security)
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                SettingsToggleRow(
                    title = "Noise / TLS 1.3 Transport Encryption",
                    subtitle = "End-to-end encrypted packet stream with ephemeral Curve25519 keys",
                    checked = settings.noiseTlsEncryption,
                    onCheckedChange = { onSettingsChange(settings.copy(noiseTlsEncryption = it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkNavyBackground)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Local: No accounts, no analytics, no remote servers.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = TextMuted, fontSize = 10.sp, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DarkNavyBackground,
                checkedTrackColor = CyanAccent,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkNavyBackground
            )
        )
    }
}
