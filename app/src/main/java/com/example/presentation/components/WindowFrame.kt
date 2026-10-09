package com.example.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.FilterNone
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.domain.model.SnapState
import com.example.domain.model.WindowState
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WindowBorder
import com.example.ui.theme.WindowHeaderDark
import kotlin.math.roundToInt

@Composable
fun WindowFrame(
    windowState: WindowState,
    isFocused: Boolean,
    screenWidth: Float,
    screenHeight: Float,
    onFocus: () -> Unit,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    onSnapLeft: () -> Unit,
    onSnapRight: () -> Unit,
    onMove: (Float, Float) -> Unit,
    onResize: (Float, Float) -> Unit,
    content: @Composable () -> Unit
) {
    if (windowState.isMinimized) return

    val borderColor = if (isFocused) CyanAccent.copy(alpha = 0.5f) else WindowBorder
    val shadowElevation = if (isFocused) 16.dp else 6.dp

    Box(
        modifier = Modifier
            .offset { IntOffset(windowState.x.roundToInt(), windowState.y.roundToInt()) }
            .size(windowState.width.dp, windowState.height.dp)
            .zIndex(windowState.zIndex.toFloat())
            .shadow(shadowElevation, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .background(DarkNavyBackground)
            .clickable { onFocus() }
            .testTag("window_${windowState.app.id}")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Window Titlebar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(WindowHeaderDark)
                    .pointerInput(windowState.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onFocus()
                            onMove(dragAmount.x, dragAmount.y)
                        }
                    }
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: App Icon & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = windowState.app.icon,
                        contentDescription = null,
                        tint = if (isFocused) CyanAccent else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = windowState.title,
                        color = if (isFocused) TextPrimary else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = if (isFocused) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }

                // Window Controls (Snap, Minimize, Maximize, Close)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Snap Left button
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onSnapLeft() }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.dp, TextMuted, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp, 12.dp)
                                    .background(CyanAccent.copy(alpha = 0.6f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Snap Right button
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onSnapRight() }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .border(1.dp, TextMuted, RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp, 12.dp)
                                    .align(Alignment.CenterEnd)
                                    .background(CyanAccent.copy(alpha = 0.6f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Minimize
                    IconButton(
                        onClick = onMinimize,
                        modifier = Modifier.size(26.dp).testTag("btn_min_${windowState.app.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HorizontalRule,
                            contentDescription = "Minimize",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Maximize / Restore
                    IconButton(
                        onClick = onToggleMaximize,
                        modifier = Modifier.size(26.dp).testTag("btn_max_${windowState.app.id}")
                    ) {
                        Icon(
                            imageVector = if (windowState.isMaximized) Icons.Default.FilterNone else Icons.Default.CropSquare,
                            contentDescription = "Maximize",
                            tint = TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }

                    // Close
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(26.dp).testTag("btn_close_${windowState.app.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Window Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }

        // Bottom-Right Corner Resize Grip Handle
        if (!windowState.isMaximized) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .align(Alignment.BottomEnd)
                    .pointerInput(windowState.id) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onResize(dragAmount.x, dragAmount.y)
                        }
                    }
                    .padding(4.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(
                    imageVector = Icons.Default.OpenWith,
                    contentDescription = "Resize",
                    tint = CyanAccent.copy(alpha = 0.5f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
