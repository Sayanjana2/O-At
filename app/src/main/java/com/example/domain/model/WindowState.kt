package com.example.domain.model

enum class SnapState {
    NONE,
    LEFT_HALF,
    RIGHT_HALF
}

data class WindowState(
    val id: String,
    val app: DesktopApp,
    val title: String = app.title,
    val x: Float = 60f,
    val y: Float = 40f,
    val width: Float = app.defaultWidth,
    val height: Float = app.defaultHeight,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val snapState: SnapState = SnapState.NONE,
    val zIndex: Int = 1,
    // Saved coordinates before maximize/snap to allow clean restore
    val restoredX: Float = 60f,
    val restoredY: Float = 40f,
    val restoredWidth: Float = app.defaultWidth,
    val restoredHeight: Float = app.defaultHeight
)
