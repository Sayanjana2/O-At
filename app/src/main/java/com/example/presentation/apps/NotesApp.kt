package com.example.presentation.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NotesApp(
    text: String,
    onTextChange: (String) -> Unit
) {
    val wordCount = if (text.isBlank()) 0 else text.trim().split("\\s+".toRegex()).size
    val charCount = text.length

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(10.dp)
            .testTag("notes_app")
    ) {
        // Toolbar
        Surface(
            color = GlassSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onTextChange("$text\n**Bold Text**") },
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatBold,
                            contentDescription = "Bold",
                            tint = TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { onTextChange("$text\n- [ ] Task item") },
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = "Bullet",
                            tint = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Saved",
                        tint = SuccessGreen
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Auto-saved locally • $wordCount words",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Editor
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("notes_input_field"),
            textStyle = TextStyle(
                color = TextPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.SansSerif,
                lineHeight = 20.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkNavyBackground,
                unfocusedContainerColor = DarkNavyBackground,
                focusedBorderColor = CyanAccent.copy(alpha = 0.5f),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = CyanAccent
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = "Type notes, markdown documentation, or ideas...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        )
    }
}
