package com.example.presentation.apps

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
fun BrowserApp(
    url: String,
    onUrlChange: (String) -> Unit
) {
    var inputUrl by remember(url) { mutableStateOf(url) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .padding(10.dp)
            .testTag("browser_app")
    ) {
        // Browser Navigation Bar
        Surface(
            color = GlassSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Back */ }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { /* Forward */ }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { onUrlChange(inputUrl) }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reload", tint = CyanAccent, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.width(4.dp))

                // URL Input Field
                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    singleLine = true,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("browser_address_bar"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = DarkNavyBackground,
                        unfocusedContainerColor = DarkNavyBackground,
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    shape = RoundedCornerShape(6.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextPrimary)
                )

                IconButton(onClick = { /* Bookmark */ }, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bookmarks Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("GitHub", "Docs", "Localhost:3000", "Figma", "Reddit").forEach { site ->
                AssistChip(
                    onClick = {
                        val newUrl = "https://${site.lowercase().replace(":", "-")}.com"
                        inputUrl = newUrl
                        onUrlChange(newUrl)
                    },
                    label = { Text(text = site, fontSize = 10.sp, color = TextSecondary) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = GlassSurfaceElevated),
                    border = null,
                    modifier = Modifier.height(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Rendered Web Page View Canvas
        Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Deskify Web Portal",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Rendering viewport: $inputUrl",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Desktop User-Agent enabled • Hardware Accelerated WebGL",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}
