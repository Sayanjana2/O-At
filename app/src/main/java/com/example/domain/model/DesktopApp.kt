package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class DesktopApp(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val defaultWidth: Float = 540f,
    val defaultHeight: Float = 380f
) {
    FILES(
        id = "files",
        title = "Files & Transfer",
        description = "Drag & drop files to PC, browse local & shared storage",
        icon = Icons.Default.Folder,
        defaultWidth = 560f,
        defaultHeight = 400f
    ),
    NOTES(
        id = "notes",
        title = "Deskify Notes",
        description = "Rich distraction-free workspace with local autosave",
        icon = Icons.Default.Description,
        defaultWidth = 500f,
        defaultHeight = 380f
    ),
    TERMINAL(
        id = "terminal",
        title = "ADB Bridge Shell",
        description = "Desktop CLI, ADB tunnel diagnostics, system telemetry",
        icon = Icons.Default.Code,
        defaultWidth = 580f,
        defaultHeight = 360f
    ),
    MONITOR(
        id = "monitor",
        title = "Performance & Stream",
        description = "Live H.264 stream bitrate, latency gauge (<80ms), FPS counter",
        icon = Icons.Default.MonitorHeart,
        defaultWidth = 540f,
        defaultHeight = 390f
    ),
    BROWSER(
        id = "browser",
        title = "Web Portal",
        description = "Desktop browser experience with tabs & responsive view",
        icon = Icons.Default.Language,
        defaultWidth = 600f,
        defaultHeight = 420f
    ),
    MEDIA(
        id = "media",
        title = "Media Studio",
        description = "Display photos & video player with playback controls",
        icon = Icons.Default.PlayCircle,
        defaultWidth = 520f,
        defaultHeight = 370f
    ),
    SETTINGS(
        id = "settings",
        title = "Deskify Settings",
        description = "Resolution, framerate, video codec, input mapping",
        icon = Icons.Default.Settings,
        defaultWidth = 520f,
        defaultHeight = 440f
    )
}
