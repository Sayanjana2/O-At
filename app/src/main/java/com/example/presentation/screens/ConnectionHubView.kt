package com.example.presentation.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ConnectionProtocol
import com.example.domain.model.ReceiverDevice
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DeepGlassSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun ConnectionHubView(
    activeReceiver: ReceiverDevice?,
    availableReceivers: List<ReceiverDevice>,
    pairingCode: String,
    onRegenerateCode: () -> Unit,
    onConnectReceiver: (ReceiverDevice) -> Unit,
    onBackToDesktop: () -> Unit
) {
    var hasUserAuthorizedProjection by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
            .testTag("connection_hub_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToDesktop,
                    modifier = Modifier.size(36.dp).testTag("hub_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Deskify Connection Hub",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "USB • Wi-Fi • HDMI / USB-C DisplayPort",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SuccessGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Online", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security Authorization Banner (Prompt on phone before connection starts)
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (hasUserAuthorizedProjection) GlassSurfaceElevated else WarningAmber.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (hasUserAuthorizedProjection) GlassBorder else WarningAmber.copy(alpha = 0.5f),
                    RoundedCornerShape(12.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (hasUserAuthorizedProjection) Icons.Default.CheckCircle else Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (hasUserAuthorizedProjection) SuccessGreen else WarningAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (hasUserAuthorizedProjection) "Device Security Verified" else "Explicit Permission Required",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (hasUserAuthorizedProjection) "VirtualDisplay authorized with TLS Noise protocol handshake." else "Confirm desktop projection session to prevent unauthorized access.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Button(
                    onClick = { hasUserAuthorizedProjection = !hasUserAuthorizedProjection },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasUserAuthorizedProjection) DarkNavyBackground else CyanAccent,
                        contentColor = if (hasUserAuthorizedProjection) CyanAccent else DarkNavyBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (hasUserAuthorizedProjection) "Authorized" else "Grant Access",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4 Connection Modes Grid (USB AOA, USB ADB, Wi-Fi mDNS, HDMI)
        Text(
            text = "Supported Transmission Channels",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionChannelCard(
                modifier = Modifier.weight(1f),
                title = "USB AOA 2.0",
                desc = "Plug & Play direct USB",
                latency = "< 15 ms",
                icon = Icons.Default.Usb,
                isActive = true
            )
            ConnectionChannelCard(
                modifier = Modifier.weight(1f),
                title = "Wi-Fi mDNS",
                desc = "Local LAN with PIN",
                latency = "< 45 ms",
                icon = Icons.Default.Wifi,
                isActive = false
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConnectionChannelCard(
                modifier = Modifier.weight(1f),
                title = "HDMI / USB-C DP",
                desc = "Hardware DisplayPort",
                latency = "< 5 ms",
                icon = Icons.Default.Tv,
                isActive = false
            )
            ConnectionChannelCard(
                modifier = Modifier.weight(1f),
                title = "Wireless Cast",
                desc = "Miracast / Cast Screen",
                latency = "< 70 ms",
                icon = Icons.Default.Cast,
                isActive = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6-digit Wi-Fi Pairing Code Display Card
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wi-Fi 6-Digit Pairing Code",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Enter this code in Deskify Receiver on your PC / Mac / Linux",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onRegenerateCode,
                        modifier = Modifier.size(32.dp).testTag("btn_regen_code")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = DarkNavyBackground,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pairingCode,
                            color = CyanAccent,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 6.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Available Receivers List
        Text(
            text = "Discovered Desktop Receivers",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            availableReceivers.forEach { receiver ->
                val isCurrent = activeReceiver?.id == receiver.id
                ReceiverRow(
                    receiver = receiver,
                    isCurrent = isCurrent,
                    onConnect = { onConnectReceiver(receiver) }
                )
            }
        }
    }
}

@Composable
private fun ConnectionChannelCard(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    latency: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean
) {
    Card(
        modifier = modifier.border(
            1.dp,
            if (isActive) CyanAccent else GlassBorder,
            RoundedCornerShape(10.dp)
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) CyanAccent.copy(alpha = 0.1f) else GlassSurfaceElevated
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) CyanAccent else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = latency,
                    color = if (isActive) SuccessGreen else TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ReceiverRow(
    receiver: ReceiverDevice,
    isCurrent: Boolean,
    onConnect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isCurrent) CyanAccent.copy(alpha = 0.12f) else GlassSurfaceElevated)
            .border(
                1.dp,
                if (isCurrent) CyanAccent.copy(alpha = 0.5f) else GlassBorder,
                RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = receiver.name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${receiver.os} • ${receiver.protocol.label} • ${receiver.ipAddress}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Button(
            onClick = onConnect,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCurrent) SuccessGreen else CyanAccent,
                contentColor = DarkNavyBackground
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isCurrent) "Connected" else "Connect",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
