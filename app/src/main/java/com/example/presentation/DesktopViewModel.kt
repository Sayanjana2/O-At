package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DesktopFileItem
import com.example.data.DesktopRepository
import com.example.domain.model.DesktopApp
import com.example.domain.model.DesktopSettings
import com.example.domain.model.NotificationModel
import com.example.domain.model.ReceiverDevice
import com.example.domain.model.SnapState
import com.example.domain.model.StreamTelemetry
import com.example.domain.model.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ViewMode {
    DESKTOP,
    PHONE_TOUCHPAD,
    CONNECTION_HUB,
    RECEIVER_DOCS,
    ONBOARDING
}

data class TerminalLine(
    val text: String,
    val isCommand: Boolean = false,
    val isSuccess: Boolean = false,
    val isError: Boolean = false
)

class DesktopViewModel(private val repository: DesktopRepository) : ViewModel() {

    private val _viewMode = MutableStateFlow(ViewMode.DESKTOP)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()

    private val _windows = MutableStateFlow<List<WindowState>>(
        listOf(
            WindowState(
                id = "win-monitor",
                app = DesktopApp.MONITOR,
                x = 40f,
                y = 30f,
                width = 540f,
                height = 370f,
                zIndex = 2
            ),
            WindowState(
                id = "win-files",
                app = DesktopApp.FILES,
                x = 120f,
                y = 80f,
                width = 550f,
                height = 380f,
                zIndex = 3
            )
        )
    )
    val windows: StateFlow<List<WindowState>> = _windows.asStateFlow()

    private val _focusedWindowId = MutableStateFlow<String?>("win-files")
    val focusedWindowId: StateFlow<String?> = _focusedWindowId.asStateFlow()

    private val _isStartMenuOpen = MutableStateFlow(false)
    val isStartMenuOpen: StateFlow<Boolean> = _isStartMenuOpen.asStateFlow()

    private val _isNotificationPanelOpen = MutableStateFlow(false)
    val isNotificationPanelOpen: StateFlow<Boolean> = _isNotificationPanelOpen.asStateFlow()

    private val _isQuickSettingsOpen = MutableStateFlow(false)
    val isQuickSettingsOpen: StateFlow<Boolean> = _isQuickSettingsOpen.asStateFlow()

    private val _isCalendarOpen = MutableStateFlow(false)
    val isCalendarOpen: StateFlow<Boolean> = _isCalendarOpen.asStateFlow()

    // Notes app content
    private val _notesText = MutableStateFlow(
        """# Deskify Architecture & Sprint Notes
- [x] Low-latency H.264 video pipeline (<15ms on USB AOA)
- [x] VirtualDisplay independent freeform multi-window compositor
- [x] Phone usable as DeX-style multi-touch trackpad & keyboard
- [x] Zero-cloud local security with Noise protocol handshake
- [ ] Windows/macOS/Linux receiver build targets
"""
    )
    val notesText: StateFlow<String> = _notesText.asStateFlow()

    // Terminal app session
    private val _terminalLines = MutableStateFlow<List<TerminalLine>>(
        listOf(
            TerminalLine("Deskify ADB Bridge Shell [v1.0.0-core]"),
            TerminalLine("Connected to Receiver via Direct USB (AOA 2.0)."),
            TerminalLine("Type 'help' to see available diagnostics commands.\n"),
            TerminalLine("$ stat", isCommand = true),
            TerminalLine("Stream: 1920x1080@60fps | Codec: H.264 HW | Latency: 13.2 ms", isSuccess = true)
        )
    )
    val terminalLines: StateFlow<List<TerminalLine>> = _terminalLines.asStateFlow()

    // Browser URL
    private val _browserUrl = MutableStateFlow("https://deskify.internal/dashboard")
    val browserUrl: StateFlow<String> = _browserUrl.asStateFlow()

    // Telemetry from repository
    val telemetry: StateFlow<StreamTelemetry> = repository.telemetryFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StreamTelemetry()
        )

    val notifications: StateFlow<List<NotificationModel>> = repository.notifications
    val availableReceivers: StateFlow<List<ReceiverDevice>> = repository.availableReceivers
    val activeReceiver: StateFlow<ReceiverDevice?> = repository.activeReceiver
    val wifiPairingCode: StateFlow<String> = repository.wifiPairingCode
    val settings: StateFlow<DesktopSettings> = repository.settings
    val files: StateFlow<List<DesktopFileItem>> = repository.files

    private var maxZIndex = 3

    fun setViewMode(mode: ViewMode) {
        _viewMode.value = mode
        closeAllFlyouts()
    }

    fun openApp(app: DesktopApp) {
        closeAllFlyouts()
        val existing = _windows.value.find { it.app == app }
        if (existing != null) {
            // Restore and focus
            focusWindow(existing.id)
            if (existing.isMinimized) {
                _windows.value = _windows.value.map {
                    if (it.id == existing.id) it.copy(isMinimized = false) else it
                }
            }
            return
        }

        maxZIndex++
        val offsetFactor = (_windows.value.size % 5) * 35f
        val newWindow = WindowState(
            id = "win-${app.id}-${UUID.randomUUID().toString().take(4)}",
            app = app,
            title = app.title,
            x = (60f + offsetFactor).coerceAtMost(300f),
            y = (40f + offsetFactor).coerceAtMost(200f),
            width = app.defaultWidth,
            height = app.defaultHeight,
            zIndex = maxZIndex
        )
        _windows.value = _windows.value + newWindow
        _focusedWindowId.value = newWindow.id
    }

    fun focusWindow(windowId: String) {
        maxZIndex++
        _windows.value = _windows.value.map {
            if (it.id == windowId) it.copy(zIndex = maxZIndex, isMinimized = false) else it
        }
        _focusedWindowId.value = windowId
    }

    fun closeWindow(windowId: String) {
        _windows.value = _windows.value.filter { it.id != windowId }
        if (_focusedWindowId.value == windowId) {
            _focusedWindowId.value = _windows.value.maxByOrNull { it.zIndex }?.id
        }
    }

    fun minimizeWindow(windowId: String) {
        _windows.value = _windows.value.map {
            if (it.id == windowId) it.copy(isMinimized = true) else it
        }
        if (_focusedWindowId.value == windowId) {
            _focusedWindowId.value = _windows.value.filter { !it.isMinimized }.maxByOrNull { it.zIndex }?.id
        }
    }

    fun toggleMaximizeWindow(windowId: String, screenWidth: Float = 1200f, screenHeight: Float = 800f) {
        _windows.value = _windows.value.map { win ->
            if (win.id == windowId) {
                if (win.isMaximized) {
                    win.copy(
                        isMaximized = false,
                        snapState = SnapState.NONE,
                        x = win.restoredX,
                        y = win.restoredY,
                        width = win.restoredWidth,
                        height = win.restoredHeight
                    )
                } else {
                    win.copy(
                        isMaximized = true,
                        snapState = SnapState.NONE,
                        restoredX = win.x,
                        restoredY = win.y,
                        restoredWidth = win.width,
                        restoredHeight = win.height,
                        x = 0f,
                        y = 0f,
                        width = screenWidth,
                        height = (screenHeight - 56f).coerceAtLeast(300f)
                    )
                }
            } else win
        }
        focusWindow(windowId)
    }

    fun snapWindow(windowId: String, snap: SnapState, screenWidth: Float = 1200f, screenHeight: Float = 800f) {
        _windows.value = _windows.value.map { win ->
            if (win.id == windowId) {
                val availableHeight = (screenHeight - 56f).coerceAtLeast(300f)
                val halfWidth = screenWidth / 2f
                when (snap) {
                    SnapState.LEFT_HALF -> win.copy(
                        snapState = SnapState.LEFT_HALF,
                        isMaximized = false,
                        x = 0f,
                        y = 0f,
                        width = halfWidth,
                        height = availableHeight
                    )
                    SnapState.RIGHT_HALF -> win.copy(
                        snapState = SnapState.RIGHT_HALF,
                        isMaximized = false,
                        x = halfWidth,
                        y = 0f,
                        width = halfWidth,
                        height = availableHeight
                    )
                    SnapState.NONE -> win.copy(
                        snapState = SnapState.NONE,
                        x = win.restoredX,
                        y = win.restoredY,
                        width = win.restoredWidth,
                        height = win.restoredHeight
                    )
                }
            } else win
        }
        focusWindow(windowId)
    }

    fun moveWindow(windowId: String, dx: Float, dy: Float) {
        _windows.value = _windows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                win.copy(
                    x = (win.x + dx).coerceAtLeast(0f),
                    y = (win.y + dy).coerceAtLeast(0f),
                    snapState = SnapState.NONE
                )
            } else win
        }
    }

    fun resizeWindow(windowId: String, dw: Float, dh: Float) {
        _windows.value = _windows.value.map { win ->
            if (win.id == windowId && !win.isMaximized) {
                win.copy(
                    width = (win.width + dw).coerceIn(280f, 1600f),
                    height = (win.height + dh).coerceIn(220f, 1000f),
                    snapState = SnapState.NONE
                )
            } else win
        }
    }

    fun minimizeAllWindows() {
        val allMinimized = _windows.value.all { it.isMinimized }
        _windows.value = _windows.value.map { it.copy(isMinimized = !allMinimized) }
        closeAllFlyouts()
    }

    fun toggleStartMenu() {
        val next = !_isStartMenuOpen.value
        closeAllFlyouts()
        _isStartMenuOpen.value = next
    }

    fun toggleNotificationPanel() {
        val next = !_isNotificationPanelOpen.value
        closeAllFlyouts()
        _isNotificationPanelOpen.value = next
    }

    fun toggleQuickSettings() {
        val next = !_isQuickSettingsOpen.value
        closeAllFlyouts()
        _isQuickSettingsOpen.value = next
    }

    fun toggleCalendar() {
        val next = !_isCalendarOpen.value
        closeAllFlyouts()
        _isCalendarOpen.value = next
    }

    fun closeAllFlyouts() {
        _isStartMenuOpen.value = false
        _isNotificationPanelOpen.value = false
        _isQuickSettingsOpen.value = false
        _isCalendarOpen.value = false
    }

    fun updateNotesContent(text: String) {
        _notesText.value = text
    }

    fun setBrowserUrl(url: String) {
        _browserUrl.value = url
    }

    fun executeTerminalCommand(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isEmpty()) return

        val newLines = _terminalLines.value.toMutableList()
        newLines.add(TerminalLine("$ $trimmed", isCommand = true))

        val parts = trimmed.split(" ")
        when (parts[0].lowercase()) {
            "help" -> {
                newLines.add(TerminalLine("Deskify Diagnostic CLI Commands:"))
                newLines.add(TerminalLine("  stat         - Live stream latency, FPS, and encoder state"))
                newLines.add(TerminalLine("  devices      - List paired and detected desktop receivers"))
                newLines.add(TerminalLine("  resolution   - Query active VirtualDisplay framebuffer"))
                newLines.add(TerminalLine("  apps         - List all open desktop window processes"))
                newLines.add(TerminalLine("  launch <app> - Launch window (files, notes, monitor, browser)"))
                newLines.add(TerminalLine("  close <app>  - Terminate window process"))
                newLines.add(TerminalLine("  ping         - Test round-trip latency to receiver"))
                newLines.add(TerminalLine("  clear        - Clear terminal buffer"))
            }
            "stat", "status" -> {
                val t = telemetry.value
                newLines.add(
                    TerminalLine(
                        "Status: ONLINE | ${t.codec} | Latency: ${t.latencyMs}ms (<${t.targetLatencyMs}ms target) | FPS: ${t.fps} | Bitrate: ${t.bitrateMbps} Mbps",
                        isSuccess = true
                    )
                )
            }
            "devices" -> {
                availableReceivers.value.forEach {
                    newLines.add(TerminalLine("• ${it.name} [${it.os}] via ${it.protocol.label} (Paired: ${it.isPaired})"))
                }
            }
            "resolution" -> {
                val t = telemetry.value
                newLines.add(TerminalLine("VirtualDisplay: ${t.resolutionWidth}x${t.resolutionHeight} @ 60Hz 320dpi (DEX_MODE_ACTIVE)"))
            }
            "apps" -> {
                _windows.value.forEach {
                    newLines.add(TerminalLine("PID [${it.id.take(8)}] ${it.title} (size: ${it.width.toInt()}x${it.height.toInt()}, min: ${it.isMinimized})"))
                }
            }
            "launch" -> {
                val targetName = parts.getOrNull(1)?.lowercase() ?: ""
                val foundApp = DesktopApp.values().find { it.id.lowercase() == targetName || it.name.lowercase() == targetName }
                if (foundApp != null) {
                    openApp(foundApp)
                    newLines.add(TerminalLine("Launched ${foundApp.title} successfully.", isSuccess = true))
                } else {
                    newLines.add(TerminalLine("Unknown app: '$targetName'. Options: files, notes, terminal, monitor, browser, settings", isError = true))
                }
            }
            "close" -> {
                val targetName = parts.getOrNull(1)?.lowercase() ?: ""
                val targetWin = _windows.value.find { it.app.id.lowercase() == targetName }
                if (targetWin != null) {
                    closeWindow(targetWin.id)
                    newLines.add(TerminalLine("Closed ${targetWin.title}.", isSuccess = true))
                } else {
                    newLines.add(TerminalLine("No running window for '$targetName'.", isError = true))
                }
            }
            "ping" -> {
                newLines.add(TerminalLine("64 bytes from receiver [127.0.0.1]: seq=1 time=11.8 ms", isSuccess = true))
                newLines.add(TerminalLine("64 bytes from receiver [127.0.0.1]: seq=2 time=12.2 ms", isSuccess = true))
                newLines.add(TerminalLine("Round-trip average: 12.0 ms (Hardware USB AOA)", isSuccess = true))
            }
            "clear" -> {
                _terminalLines.value = listOf(TerminalLine("Deskify ADB Bridge Shell [v1.0.0-core]"))
                return
            }
            else -> {
                newLines.add(TerminalLine("bash: command not found: ${parts[0]}. Type 'help' for commands.", isError = true))
            }
        }

        _terminalLines.value = newLines
    }

    fun updateSettings(newSettings: DesktopSettings) {
        repository.updateSettings(newSettings)
    }

    fun dismissNotification(id: String) {
        repository.dismissNotification(id)
    }

    fun clearNotifications() {
        repository.clearAllNotifications()
    }

    fun toggleFileStaging(id: String) {
        repository.toggleFileStaging(id)
    }

    fun regeneratePairingCode() {
        repository.regeneratePairingCode()
    }

    fun connectReceiver(receiver: ReceiverDevice) {
        repository.connectToReceiver(receiver)
    }
}
