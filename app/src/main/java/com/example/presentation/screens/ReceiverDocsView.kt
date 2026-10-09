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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DeepGlassSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

enum class DocsTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ARCHITECTURE("Architecture", Icons.Default.Hub),
    BUILD_GUIDE("Receiver Guide", Icons.Default.Code),
    PROTOCOLS("Wire Protocols", Icons.Default.Description),
    LIMITATIONS("Limitations & Sec", Icons.Default.Warning)
}

@Composable
fun ReceiverDocsView(
    onBackToDesktop: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DocsTab.ARCHITECTURE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(14.dp)
            .testTag("receiver_docs_screen")
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
                    modifier = Modifier.size(36.dp).testTag("docs_back_button")
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
                        text = "Deskify Engineering Spec",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cross-Platform Receiver & Architecture Blueprint",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DocsTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) CyanAccent.copy(alpha = 0.2f) else GlassSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) CyanAccent else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) CyanAccent else TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = tab.label,
                            color = if (isSelected) CyanAccent else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTab) {
                DocsTab.ARCHITECTURE -> ArchitectureSection()
                DocsTab.BUILD_GUIDE -> BuildGuideSection()
                DocsTab.PROTOCOLS -> ProtocolsSection()
                DocsTab.LIMITATIONS -> LimitationsSection()
            }
        }
    }
}

@Composable
private fun ArchitectureSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DocsCard(title = "1. Cross-Platform System Architecture") {
            Text(
                text = """
+-------------------------------------------------------------+
|                      DESKIFY SENDER (ANDROID)               |
|  - VirtualDisplay Compositor (android.hardware.display)     |
|  - Freeform Multi-Window Desktop Engine (Compose M3)        |
|  - MediaCodec Hardware Encoder (H.264 / HEVC Baseline/High) |
|  - Input Event Dispatcher (uinput / Accessibility bridge)   |
|  - Phone Trackpad & Remote Control Mode (Independent Screen)|
+-----------------------------+-------------------------------+
                              |
    Transport Options:        |  USB AOA 2.0 (<15ms)
                              |  USB ADB Reverse Port 8089 (<25ms)
                              |  Wi-Fi mDNS + Noise Protocol (<45ms)
                              |  HDMI DisplayPort Alt-Mode (<5ms)
                              v
+-------------------------------------------------------------+
|               DESKIFY RECEIVER (WINDOWS / MAC / LINUX)      |
|  - Core Engine: Rust + Tauri (zero Electron bloat, <15MB)  |
|  - Hardware Decoder: FFmpeg / libavcodec via DirectX/Metal  |
|  - Native Windows / Linux Event Injection (SendInput/evdev) |
|  - Clipboard Syncer & Drag-and-Drop Virtual Drive Server    |
+-------------------------------------------------------------+
                """.trimIndent(),
                color = CyanAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp
            )
        }

        DocsCard(title = "2. Tech Stack Justification & SOLID Principles") {
            BulletItem(
                title = "Receiver: Tauri 2.0 (Rust + TypeScript)",
                body = "Eliminates ~150MB of Electron Chromium overhead. Rust guarantees zero-cost abstractions, memory safety, and native low-latency USB AOA / WinUSB socket polling."
            )
            BulletItem(
                title = "Sender: Kotlin Jetpack Compose + VirtualDisplay",
                body = "Leverages Android 10+ Multi-Display API without requiring root. Compose enables declarative glassmorphic multi-window rendering at 60-120 FPS."
            )
            BulletItem(
                title = "Hardware H.264 Encoder (Android MediaCodec)",
                body = "Bypasses CPU rendering by directly binding the Surface output of the VirtualDisplay to MediaCodec, achieving <15ms frame generation."
            )
            BulletItem(
                title = "Adherence to SOLID Principles",
                body = "Single Responsibility: Transports (USB, Wi-Fi, HDMI) decoupled from window compositor. Open/Closed: Plug-in new decoders or input sinks without rewriting core."
            )
        }
    }
}

@Composable
private fun BuildGuideSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DocsCard(title = "Windows Receiver MVP (Over USB First)") {
            Text(
                text = """
Step 1: Install Rust & Node.js prerequisites
$ curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
$ npm install -g @tauri-apps/cli

Step 2: Clone & build Deskify Receiver
$ git clone https://github.com/deskify/receiver.git
$ cd receiver
$ npm install
$ cargo tauri build --target x86_64-pc-windows-msvc

Step 3: Connect Android phone via USB
1. Plug USB cable into PC (ensure USB Debugging is ON).
2. The phone auto-detects USB AOA / ADB reverse socket.
3. Deskify launches on Windows with full keyboard/mouse forwarding!
                """.trimIndent(),
                color = SkyBlue,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }

        DocsCard(title = "Cross-Platform Roadmap (macOS, Linux, HDMI)") {
            BulletItem(
                title = "macOS Sequoia / Sonoma (Q4 2026)",
                body = "Uses VideoToolbox for hardware H.264 decode and IOKit for USB HID mouse & keyboard event passthrough."
            )
            BulletItem(
                title = "Linux (Wayland & X11 / Ubuntu / Arch)",
                body = "Direct integration with libevdev and VA-API hardware decoding. Wayland floating window manager support."
            )
            BulletItem(
                title = "HDMI / USB-C DisplayPort Output",
                body = "Plug phone directly into an external USB-C monitor or TV dock. Deskify detects secondary display via Android Presentation API and launches immediately without a computer receiver!"
            )
        }
    }
}

@Composable
private fun ProtocolsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DocsCard(title = "Low-Latency Framing Protocol (Deskify Packet V1)") {
            Text(
                text = """
Binary Framing Header (16 Bytes):
[ 0..3  ] Magic: 0x44 0x45 0x53 0x4B ('DESK')
[ 4..5  ] Version: 0x0001
[ 6..7  ] Packet Type:
          0x01 = VIDEO_NALU (H.264/H.265 Frame)
          0x02 = AUDIO_PCM  (Opus/Linear PCM)
          0x03 = INPUT_EVENT (Mouse Move/Click, Key)
          0x04 = CLIPBOARD_SYNC (UTF-8 / Image)
          0x05 = HEARTBEAT_PING (Round-trip check)
[ 8..11 ] Sequence ID: uint32
[ 12..15] Payload Length: uint32
[ 16..N ] Payload Data (Encrypted via Noise Protocol)
                """.trimIndent(),
                color = CyanAccent,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
        }

        DocsCard(title = "Mouse & Keyboard Input Event Spec") {
            BulletItem(
                title = "Mouse Move",
                body = "Type: 0x03, SubType: 0x01 | Payload: float32 x, float32 y, int16 deltaX, int16 deltaY"
            )
            BulletItem(
                title = "Mouse Click / Scroll",
                body = "Type: 0x03, SubType: 0x02 | Payload: uint8 button (1=Left, 2=Right, 3=Middle), uint8 state (Down/Up), int8 scrollWheel"
            )
            BulletItem(
                title = "Keyboard Key Press",
                body = "Type: 0x03, SubType: 0x03 | Payload: uint16 keyCode, uint8 modifiers (Shift, Ctrl, Alt, Meta), uint8 action"
            )
        }
    }
}

@Composable
private fun LimitationsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DocsCard(title = "Platform Limitations") {
            BulletItem(
                title = "iOS Limitation (Strict Mirroring Only)",
                body = "iOS does not support creating a secondary VirtualDisplay for third-party apps. ReplayKit & AirPlay only permit basic screen mirroring. Deskify labels iOS as 'Limited Mode' with an explicit disclaimer."
            )
            BulletItem(
                title = "Android OEM USB-C Restrictions",
                body = "Budget Android devices lacking USB 3.1 DP-Alt mode cannot output HDMI video directly; for those devices, Deskify uses USB AOA or Wi-Fi streaming to a PC receiver."
            )
            BulletItem(
                title = "Input Injection Permissions",
                body = "Injecting touches into other third-party phone apps requires ADB access or an Accessibility service. Deskify handles native desktop apps and VirtualDisplay instances directly."
            )
        }

        DocsCard(title = "Security & Vulnerability Analysis") {
            BulletItem(
                title = "Zero Cloud & Local Only",
                body = "No telemetry, accounts, or analytics. Traffic never traverses the public internet."
            )
            BulletItem(
                title = "Noise Protocol (XX Pattern)",
                body = "Mutual authentication with ephemeral 25519 keys protects against LAN sniffing and man-in-the-middle attacks on public Wi-Fi."
            )
            BulletItem(
                title = "Explicit Phone Prompt",
                body = "Requires user confirmation on the phone screen before any video feed or input bridge is established."
            )
        }
    }
}

@Composable
private fun DocsCard(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun BulletItem(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = "• $title",
            color = CyanAccent,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = body,
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(start = 10.dp, top = 2.dp)
        )
    }
}
