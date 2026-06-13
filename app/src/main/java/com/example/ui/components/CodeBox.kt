package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.WarningAmber

@Composable
fun CodeBox(
    code: String,
    language: String,
    title: String,
    isBookmarked: Boolean = false,
    onBookmarkToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()

    // Base code highlighting parsing logic
    val highlightedText = buildAnnotatedString {
        val lines = code.split("\n")
        lines.forEachIndexed { lineIdx, line ->
            var cursor = 0
            val words = line.split(Regex("(?<=\\b)|(?=\\b)|(?<=\\W)|(?=\\W)"))
            
            words.forEach { word ->
                when {
                    word in listOf("include", "import", "package", "class", "public", "private", "protected", "void", "int", "float", "double", "char", "def", "return", "if", "else", "for", "while", "null", "NULL", "new", "abstract", "extends", "static", "Optional", "final") -> {
                        pushStyle(SpanStyle(color = SecondaryBlue, fontWeight = FontWeight.Bold))
                        append(word)
                        pop()
                    }
                    word.startsWith("#") -> {
                        pushStyle(SpanStyle(color = Color(0xFF38BDF8), fontWeight = FontWeight.SemiBold))
                        append(word)
                        pop()
                    }
                    word.startsWith("//") || word.startsWith("/*") -> {
                        pushStyle(SpanStyle(color = Color(0xFF64748B)))
                        append(word)
                        pop()
                    }
                    word.startsWith("\"") && word.endsWith("\"") || (word.startsWith("'") && word.endsWith("'")) -> {
                        pushStyle(SpanStyle(color = SuccessGreenLight))
                        append(word)
                        pop()
                    }
                    word.toIntOrNull() != null -> {
                        pushStyle(SpanStyle(color = WarningAmber))
                        append(word)
                        pop()
                    }
                    else -> {
                        append(word)
                    }
                }
            }
            if (lineIdx < lines.size - 1) append("\n")
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(SecondaryBlue, RoundedCornerShape(50))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFE2E8F0),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${language.uppercase()})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Row {
                    if (onBookmarkToggle != null) {
                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark Code",
                                tint = if (isBookmarked) WarningAmber else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(code))
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Code Content Panel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                // Line Numbers sidebar (custom drawing divider behind)
                val linesCount = code.split("\n").size
                Column(
                    modifier = Modifier
                        .width(36.dp)
                        .drawBehind {
                            // Subtle vertical clinical-tech divider line
                            drawLine(
                                color = Color(0xFF334155),
                                start = Offset(size.width, 0f),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1f
                            )
                        }
                        .padding(end = 6.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..linesCount) {
                        Text(
                            text = i.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF475569),
                            fontSize = 12.sp,
                            textAlign = TextAlign.End
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Scrollable main code block
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState)
                        .padding(end = 12.dp)
                ) {
                    Text(
                        text = highlightedText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFF1F5F9),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
