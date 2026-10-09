package com.example.domain.model

enum class ConnectionProtocol(val label: String, val transport: String, val idealLatency: String) {
    USB_AOA("USB (AOA 2.0)", "Android Open Accessory Direct USB", "< 15 ms"),
    USB_ADB("USB (ADB Tunnel)", "Reverse Socket Port 8089", "< 25 ms"),
    WIFI_MDNS("Wi-Fi (mDNS)", "Local Wireless LAN with 6-digit PIN", "< 45 ms"),
    HDMI_DISPLAY_PORT("HDMI / USB-C DP", "Hardware DisplayPort Alternate Mode", "< 5 ms"),
    WIRELESS_CAST("Miracast / Cast", "Direct P2P Screen Projection", "< 70 ms")
}

enum class ConnectionStatus {
    DISCONNECTED,
    DETECTING,
    PAIRING_REQUIRED,
    HANDSHAKING,
    CONNECTED,
    ERROR
}

data class StreamTelemetry(
    val fps: Int = 60,
    val latencyMs: Int = 14,
    val targetLatencyMs: Int = 80,
    val bitrateMbps: Float = 18.5f,
    val droppedFramesPercent: Float = 0.02f,
    val resolutionWidth: Int = 1920,
    val resolutionHeight: Int = 1080,
    val codec: String = "H.264 AVC (Hardware Encoders)"
)

data class ReceiverDevice(
    val id: String,
    val name: String,
    val os: String, // Windows 11, macOS Sequoia, Linux Ubuntu
    val protocol: ConnectionProtocol,
    val ipAddress: String,
    val port: Int,
    val isAutoDetected: Boolean,
    val isPaired: Boolean,
    val fingerprint: String
)
