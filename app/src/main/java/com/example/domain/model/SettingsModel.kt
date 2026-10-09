package com.example.domain.model

enum class VideoCodec(val label: String) {
    H264("H.264 / AVC (Broadest Compatibility)"),
    HEVC("H.265 / HEVC (Lower Bandwidth)"),
    AV1("AV1 (Next-Gen Open Codec)")
}

enum class DisplayResolution(val label: String, val width: Int, val height: Int) {
    RES_1080P("1080p FHD (1920 x 1080)", 1920, 1080),
    RES_1440P("1440p 2K (2560 x 1440)", 2560, 1440),
    RES_4K("4K UHD (3840 x 2160)", 3840, 2160),
    RES_720P("720p HD (1280 x 720)", 1280, 720)
}

data class DesktopSettings(
    val resolution: DisplayResolution = DisplayResolution.RES_1080P,
    val refreshRateFps: Int = 60, // 60 or 120
    val videoCodec: VideoCodec = VideoCodec.H264,
    val bitrateMbps: Int = 18,
    val audioStreamingEnabled: Boolean = true,
    val clipboardSyncEnabled: Boolean = true,
    val touchSensitivity: Float = 1.0f,
    val noiseTlsEncryption: Boolean = true,
    val autoLaunchOnUsb: Boolean = true,
    val phoneStaysUsable: Boolean = true
)
