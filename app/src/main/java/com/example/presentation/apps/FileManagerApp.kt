package com.example.presentation.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DesktopFileItem
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DeepGlassSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FileManagerApp(
    files: List<DesktopFileItem>,
    onToggleStage: (String) -> Unit
) {
    val stagedCount = files.count { it.isStagedForTransfer }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(12.dp)
            .testTag("file_manager_app")
    ) {
        // Toolbar / Breadcrumb
        Surface(
            color = GlassSurfaceElevated,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Storage",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phone Storage / Deskify / SharedDocuments",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (stagedCount > 0) {
                    Surface(
                        color = CyanAccent.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$stagedCount staged for PC Drop",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Files List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(files, key = { it.id }) { file ->
                FileRowItem(
                    file = file,
                    onToggleStage = { onToggleStage(file.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Drag & Drop instructions banner
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepGlassSurface),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Bidirectional Drag & Drop",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Drag files directly to PC desktop, or drop PC files here.",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { /* trigger bulk sync */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = DarkNavyBackground
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("transfer_pc_button")
                ) {
                    Text(
                        text = "Send to PC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FileRowItem(
    file: DesktopFileItem,
    onToggleStage: () -> Unit
) {
    val fileIcon: ImageVector = when (file.type) {
        "folder" -> Icons.Default.Folder
        "pdf", "doc" -> Icons.Default.Description
        "image" -> Icons.Default.Image
        "code" -> Icons.Default.Code
        "video" -> Icons.Default.Movie
        else -> Icons.Default.Description
    }

    val iconColor = when (file.type) {
        "pdf" -> Color(0xFFEF4444)
        "image" -> Color(0xFF10B981)
        "code" -> CyanAccent
        "zip" -> Color(0xFFF59E0B)
        else -> Color(0xFF38BDF8)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (file.isStagedForTransfer) CyanAccent.copy(alpha = 0.08f) else GlassSurfaceElevated)
            .border(
                width = 1.dp,
                color = if (file.isStagedForTransfer) CyanAccent.copy(alpha = 0.4f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onToggleStage() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = fileIcon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = file.name,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Row {
                    Text(
                        text = file.size,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = " • ${file.dateModified}",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        IconButton(
            onClick = onToggleStage,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (file.isStagedForTransfer) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                contentDescription = "Toggle Stage",
                tint = if (file.isStagedForTransfer) SuccessGreen else TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
