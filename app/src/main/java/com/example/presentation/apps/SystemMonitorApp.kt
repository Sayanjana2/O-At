package com.example.presentation.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.StreamTelemetry
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

@Composable
fun SystemMonitorApp(
    telemetry: StreamTelemetry
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(12.dp)
            .testTag("system_monitor_app")
    ) {
        // Top Summary Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Latency Card
            TelemetryMetricCard(
                modifier = Modifier.weight(1f),
                title = "Round-Trip Latency",
                value = "${telemetry.latencyMs} ms",
                subtitle = "Target: < ${telemetry.targetLatencyMs} ms",
                accentColor = if (telemetry.latencyMs < 30) SuccessGreen else WarningAmber,
                progress = (telemetry.latencyMs / 100f).coerceIn(0.05f, 1f)
            )

            // FPS Card
            TelemetryMetricCard(
                modifier = Modifier.weight(1f),
                title = "Display Frame Rate",
                value = "${telemetry.fps} FPS",
                subtitle = "V-Sync: 60 Hz Lock",
                accentColor = CyanAccent,
                progress = (telemetry.fps / 60f).coerceIn(0f, 1f)
            )

            // Bitrate Card
            TelemetryMetricCard(
                modifier = Modifier.weight(1f),
                title = "Video Stream Bitrate",
                value = "${telemetry.bitrateMbps} Mbps",
                subtitle = "CBR Adaptive Rate",
                accentColor = SkyBlue,
                progress = (telemetry.bitrateMbps / 30f).coerceIn(0f, 1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hardware Encoder Pipeline Details
        Card(
            colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hardware Encoder Pipeline",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OPTIMAL",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EncoderDetailRow(label = "Codec", value = telemetry.codec)
                    EncoderDetailRow(label = "Resolution", value = "${telemetry.resolutionWidth}x${telemetry.resolutionHeight}")
                    EncoderDetailRow(label = "Frame Drops", value = "${telemetry.droppedFramesPercent}%")
                    EncoderDetailRow(label = "Transport", value = "USB AOA 2.0 Bulk")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Latency Waveform Graph
        Surface(
            color = DeepGlassSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Real-Time Latency Waveform (ms)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Threshold: 80 ms target",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    val gridSteps = 4
                    for (i in 0..gridSteps) {
                        val y = h * (i / gridSteps.toFloat())
                        drawLine(
                            color = Color(0x1FFFFFFF),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // 80ms Target Line (Threshold)
                    val targetY = h * 0.2f
                    drawLine(
                        color = Color(0x66F59E0B),
                        start = Offset(0f, targetY),
                        end = Offset(w, targetY),
                        strokeWidth = 2f
                    )

                    // Waveform Points (simulated smooth wave around latency)
                    val points = 24
                    val step = w / (points - 1)
                    val baseRatio = (telemetry.latencyMs / 80f).coerceIn(0.1f, 0.9f)

                    for (i in 0 until points - 1) {
                        val x1 = i * step
                        val noise1 = kotlin.math.sin(i * 0.7f + telemetry.latencyMs * 0.1f) * 8f
                        val y1 = h - (baseRatio * (h * 0.7f)) + noise1

                        val x2 = (i + 1) * step
                        val noise2 = kotlin.math.sin((i + 1) * 0.7f + telemetry.latencyMs * 0.1f) * 8f
                        val y2 = h - (baseRatio * (h * 0.7f)) + noise2

                        drawLine(
                            brush = Brush.horizontalGradient(listOf(CyanAccent, SkyBlue)),
                            start = Offset(x1, y1),
                            end = Offset(x2, y2),
                            strokeWidth = 3f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    progress: Float
) {
    Card(
        modifier = modifier.border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassSurfaceElevated),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = accentColor,
                trackColor = Color(0x33FFFFFF)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun EncoderDetailRow(label: String, value: String) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}
