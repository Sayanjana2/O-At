package com.example.data

import android.content.Context
import android.hardware.display.DisplayManager
import android.view.Display

data class ExternalDisplayInfo(
    val displayId: Int,
    val name: String,
    val width: Int,
    val height: Int,
    val refreshRate: Float,
    val isExternal: Boolean
)

class DisplayHelper(private val context: Context) {
    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager

    fun getConnectedDisplays(): List<ExternalDisplayInfo> {
        val dm = displayManager ?: return emptyList()
        val displays = dm.displays
        return displays.map { display ->
            val isExt = display.displayId != Display.DEFAULT_DISPLAY
            ExternalDisplayInfo(
                displayId = display.displayId,
                name = display.name ?: "Display #${display.displayId}",
                width = display.width,
                height = display.height,
                refreshRate = display.refreshRate,
                isExternal = isExt
            )
        }
    }

    fun hasExternalDisplay(): Boolean {
        return getConnectedDisplays().any { it.isExternal }
    }
}
