package com.example.data

import android.content.Context
import com.example.domain.model.ConnectionProtocol
import com.example.domain.model.ConnectionStatus
import com.example.domain.model.DesktopSettings
import com.example.domain.model.NotificationModel
import com.example.domain.model.ReceiverDevice
import com.example.domain.model.StreamTelemetry
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

data class DesktopFileItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: String,
    val type: String, // "folder", "pdf", "image", "doc", "code", "zip"
    val dateModified: String,
    val isStagedForTransfer: Boolean = false
)

class DesktopRepository(private val context: Context) {

    private val displayHelper = DisplayHelper(context)

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.CONNECTED)
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _activeReceiver = MutableStateFlow<ReceiverDevice?>(
        ReceiverDevice(
            id = "win-pc-01",
            name = "Studio Workstation (Win 11)",
            os = "Windows 11 Pro 64-bit",
            protocol = ConnectionProtocol.USB_AOA,
            ipAddress = "127.0.0.1 (Direct USB AOA)",
            port = 8089,
            isAutoDetected = true,
            isPaired = true,
            fingerprint = "SHA256:7D:9A:3F:82:1C:4B:99:E0:11:AB"
        )
    )
    val activeReceiver = _activeReceiver.asStateFlow()

    private val _availableReceivers = MutableStateFlow<List<ReceiverDevice>>(
        listOf(
            ReceiverDevice(
                id = "win-pc-01",
                name = "Studio Workstation (Win 11)",
                os = "Windows 11 Pro",
                protocol = ConnectionProtocol.USB_AOA,
                ipAddress = "127.0.0.1 (USB Direct)",
                port = 8089,
                isAutoDetected = true,
                isPaired = true,
                fingerprint = "SHA256:7D:9A:3F:82:1C:4B:99:E0:11:AB"
            ),
            ReceiverDevice(
                id = "macbook-m3",
                name = "MacBook Pro M3 Max",
                os = "macOS Sequoia 15.1",
                protocol = ConnectionProtocol.WIFI_MDNS,
                ipAddress = "192.168.1.142",
                port = 8089,
                isAutoDetected = true,
                isPaired = false,
                fingerprint = "SHA256:2C:8E:44:90:3A:FF:12:09:BD:01"
            ),
            ReceiverDevice(
                id = "linux-desk",
                name = "Arch Linux Desktop",
                os = "Linux (Wayland / Mutter)",
                protocol = ConnectionProtocol.USB_ADB,
                ipAddress = "127.0.0.1:8089",
                port = 8089,
                isAutoDetected = false,
                isPaired = true,
                fingerprint = "SHA256:EE:91:02:BA:45:90:CD:12:77:3A"
            )
        )
    )
    val availableReceivers = _availableReceivers.asStateFlow()

    private val _wifiPairingCode = MutableStateFlow("482 917")
    val wifiPairingCode = _wifiPairingCode.asStateFlow()

    private val _settings = MutableStateFlow(DesktopSettings())
    val settings = _settings.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationModel>>(
        listOf(
            NotificationModel(
                id = "notif-1",
                appName = "Messages",
                title = "Elena Rostova",
                message = "The architecture review meeting starts in 10 minutes. Can you share the desktop slides?",
                timestamp = "2m ago",
                category = "Message"
            ),
            NotificationModel(
                id = "notif-2",
                appName = "Deskify Bridge",
                title = "USB Stream Connected",
                message = "H.264 Hardware pipeline active. Measured latency 12.4ms @ 60 FPS.",
                timestamp = "Just now",
                category = "System"
            ),
            NotificationModel(
                id = "notif-3",
                appName = "Calendar",
                title = "Sprint Planning",
                message = "Tomorrow at 10:00 AM • Google Meet link attached",
                timestamp = "18m ago",
                category = "Calendar"
            ),
            NotificationModel(
                id = "notif-4",
                appName = "Transfer Manager",
                title = "File Ready for PC",
                message = "Presentation_Q4_Deck.pdf staged for drag-and-drop to PC receiver.",
                timestamp = "35m ago",
                category = "Transfer"
            )
        )
    )
    val notifications = _notifications.asStateFlow()

    private val _files = MutableStateFlow<List<DesktopFileItem>>(
        listOf(
            DesktopFileItem(name = "Quarterly_Financial_Report.pdf", size = "3.4 MB", type = "pdf", dateModified = "Today, 10:14 AM"),
            DesktopFileItem(name = "Architecture_Diagram_DeX.png", size = "2.1 MB", type = "image", dateModified = "Today, 09:30 AM", isStagedForTransfer = true),
            DesktopFileItem(name = "Project_Requirements.docx", size = "840 KB", type = "doc", dateModified = "Yesterday"),
            DesktopFileItem(name = "Receiver_Tauri_Source.zip", size = "14.8 MB", type = "zip", dateModified = "Oct 8, 2026"),
            DesktopFileItem(name = "main_encoder_pipeline.rs", size = "24 KB", type = "code", dateModified = "Oct 7, 2026"),
            DesktopFileItem(name = "ScreenCapture_1080p.mp4", size = "48.2 MB", type = "video", dateModified = "Oct 6, 2026")
        )
    )
    val files = _files.asStateFlow()

    // Live Stream Telemetry Flow
    val telemetryFlow: Flow<StreamTelemetry> = flow {
        var baseLatency = 13
        while (true) {
            val jitter = Random.nextInt(-2, 3)
            val currentLatency = (baseLatency + jitter).coerceIn(9, 24)
            val currentFps = if (Random.nextFloat() > 0.05f) 60 else 59
            val currentBitrate = 18.0f + Random.nextFloat() * 1.5f
            emit(
                StreamTelemetry(
                    fps = currentFps,
                    latencyMs = currentLatency,
                    targetLatencyMs = 80,
                    bitrateMbps = (currentBitrate * 10).toInt() / 10f,
                    droppedFramesPercent = 0.01f,
                    resolutionWidth = _settings.value.resolution.width,
                    resolutionHeight = _settings.value.resolution.height,
                    codec = "${_settings.value.videoCodec.name} Hardware Direct"
                )
            )
            delay(1200)
        }
    }

    fun regeneratePairingCode(): String {
        val code = "%03d %03d".format(Random.nextInt(100, 999), Random.nextInt(100, 999))
        _wifiPairingCode.value = code
        return code
    }

    fun updateSettings(newSettings: DesktopSettings) {
        _settings.value = newSettings
    }

    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun toggleFileStaging(fileId: String) {
        _files.value = _files.value.map { file ->
            if (file.id == fileId) file.copy(isStagedForTransfer = !file.isStagedForTransfer) else file
        }
    }

    fun connectToReceiver(receiver: ReceiverDevice) {
        _activeReceiver.value = receiver
        _connectionStatus.value = ConnectionStatus.CONNECTED
    }

    fun disconnect() {
        _connectionStatus.value = ConnectionStatus.DISCONNECTED
    }

    fun checkExternalDisplay(): Boolean {
        return displayHelper.hasExternalDisplay()
    }
}
