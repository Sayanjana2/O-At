package com.example.presentation.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.presentation.TerminalLine
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TerminalApp(
    lines: List<TerminalLine>,
    onExecuteCommand: (String) -> Unit
) {
    var inputCommand by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060913))
            .padding(10.dp)
            .testTag("terminal_app")
    ) {
        // Console Output Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 6.dp)
        ) {
            items(lines) { line ->
                val textColor = when {
                    line.isCommand -> CyanAccent
                    line.isSuccess -> SuccessGreen
                    line.isError -> DangerCoral
                    else -> TextSecondary
                }

                Text(
                    text = line.text,
                    color = textColor,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = if (line.isCommand) FontWeight.SemiBold else FontWeight.Normal,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }

        // Quick Command Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("stat", "devices", "resolution", "apps", "ping", "clear").forEach { cmd ->
                AssistChip(
                    onClick = { onExecuteCommand(cmd) },
                    label = {
                        Text(
                            text = cmd,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyanAccent
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = GlassSurfaceElevated
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = CyanAccent.copy(alpha = 0.3f),
                        borderWidth = 1.dp,
                        enabled = true
                    ),
                    modifier = Modifier.testTag("chip_$cmd")
                )
            }
        }

        // Input Box
        Surface(
            color = GlassSurfaceElevated,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )

                OutlinedTextField(
                    value = inputCommand,
                    onValueChange = { inputCommand = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input"),
                    placeholder = {
                        Text(
                            text = "type 'help' or command...",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = CyanAccent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputCommand.isNotBlank()) {
                                onExecuteCommand(inputCommand)
                                inputCommand = ""
                            }
                        }
                    )
                )

                IconButton(
                    onClick = {
                        if (inputCommand.isNotBlank()) {
                            onExecuteCommand(inputCommand)
                            inputCommand = ""
                        }
                    },
                    modifier = Modifier.testTag("terminal_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
