package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DesktopRepository
import com.example.domain.model.DesktopApp
import com.example.domain.model.SnapState
import com.example.presentation.DesktopViewModel
import com.example.presentation.ViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Deskify app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Deskify", appName)
    }

    @Test
    fun `desktop view model window manager open and close`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = DesktopRepository(context)
        val viewModel = DesktopViewModel(repo)

        // Open Terminal
        viewModel.openApp(DesktopApp.TERMINAL)
        val hasTerminal = viewModel.windows.value.any { it.app == DesktopApp.TERMINAL }
        assertTrue(hasTerminal)

        // Toggle Maximize
        val termWin = viewModel.windows.value.first { it.app == DesktopApp.TERMINAL }
        viewModel.toggleMaximizeWindow(termWin.id, 1920f, 1080f)
        val maximizedWin = viewModel.windows.value.first { it.id == termWin.id }
        assertTrue(maximizedWin.isMaximized)

        // Snap Left
        viewModel.snapWindow(termWin.id, SnapState.LEFT_HALF, 1920f, 1080f)
        val snappedWin = viewModel.windows.value.first { it.id == termWin.id }
        assertEquals(SnapState.LEFT_HALF, snappedWin.snapState)

        // Close
        viewModel.closeWindow(termWin.id)
        val stillExists = viewModel.windows.value.any { it.id == termWin.id }
        assertFalse(stillExists)
    }

    @Test
    fun `desktop view model terminal shell commands`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = DesktopRepository(context)
        val viewModel = DesktopViewModel(repo)

        viewModel.executeTerminalCommand("stat")
        val lines = viewModel.terminalLines.value
        assertTrue(lines.any { it.text.contains("Status: ONLINE") })

        viewModel.executeTerminalCommand("ping")
        assertTrue(viewModel.terminalLines.value.any { it.text.contains("Round-trip") })
    }

    @Test
    fun `mode switching updates viewMode state flow`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = DesktopRepository(context)
        val viewModel = DesktopViewModel(repo)

        viewModel.setViewMode(ViewMode.PHONE_TOUCHPAD)
        assertEquals(ViewMode.PHONE_TOUCHPAD, viewModel.viewMode.value)

        viewModel.setViewMode(ViewMode.CONNECTION_HUB)
        assertEquals(ViewMode.CONNECTION_HUB, viewModel.viewMode.value)

        viewModel.setViewMode(ViewMode.RECEIVER_DOCS)
        assertEquals(ViewMode.RECEIVER_DOCS, viewModel.viewMode.value)
    }
}
