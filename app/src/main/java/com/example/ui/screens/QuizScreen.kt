package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuizQuestion
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    questions: List<QuizQuestion>,
    currentIndex: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    scoreCount: Int,
    isFinished: Boolean,
    onOptionSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit,
    onBackToCourse: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interactive Practice Quiz", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackToCourse, modifier = Modifier.testTag("quiz_back_button")) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Exit Quiz", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepSlateSurface)
            )
        },
        containerColor = DeepSlateBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isFinished) {
                // Celebration Completion Layout
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MilitaryTech,
                            contentDescription = "Badge",
                            tint = SuccessGreenLight,
                            modifier = Modifier.size(64.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Exams Compiled Successfully!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val scorePercentage = if (questions.isNotEmpty()) {
                        (scoreCount.toFloat() / questions.size * 100).toInt()
                    } else 0

                    Text(
                        text = "You scored $scoreCount / ${questions.size} ($scorePercentage%) on this module track.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFCBD5E1),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Gained stats card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "XP GAINED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                Text(
                                    text = "+${scoreCount * 30} XP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreenLight
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "STREAK", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                                Text(
                                    text = "+1 Day",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9F43)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Button(
                        onClick = onBackToCourse,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_finish_return_button")
                    ) {
                        Text(text = "RETURN TO COURSE DOCK", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (questions.isNotEmpty() && currentIndex < questions.size) {
                val question = questions[currentIndex]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .widthIn(max = 500.dp)
                ) {
                    // Progress Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "QUESTION ${currentIndex + 1} OF ${questions.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Score: $scoreCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = SuccessGreenLight,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress indicator
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = SecondaryBlue,
                        trackColor = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Question Box
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer Options List
                    question.options.forEachIndexed { optIndex, optionString ->
                        val isSelected = selectedOption == optIndex
                        
                        // Highlights colors configurations based on evaluation answers
                        val (cardBg, borderStroke) = when {
                            isSubmitted && optIndex == question.correctIndex -> {
                                // Correct option is highlighted as Green
                                SuccessGreen.copy(alpha = 0.15f) to BorderStroke(2.dp, SuccessGreen)
                            }
                            isSubmitted && isSelected && optIndex != question.correctIndex -> {
                                // Selected wrong option is highlighted as Red
                                ErrorRed.copy(alpha = 0.15f) to BorderStroke(2.dp, ErrorRed)
                            }
                            isSelected -> {
                                // User actively selected option (pre-submission)
                                PrimaryBlue.copy(alpha = 0.15f) to BorderStroke(2.dp, PrimaryBlue)
                            }
                            else -> {
                                // Standard neutral state option card
                                DeepSlateSurface to BorderStroke(1.dp, Color(0xFF1E293B))
                            }
                        }

                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            border = borderStroke,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable(enabled = !isSubmitted) { onOptionSelect(optIndex) }
                                .testTag("quiz_option_$optIndex")
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Prefix alphabetical indicator A, B, C, D
                                val prefixLetter = ('A' + optIndex)
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PrimaryBlue else Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = prefixLetter.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFF94A3B8)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = optionString,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Educational Answer Explanation Card
                    AnimatedVisibility(
                        visible = isSubmitted,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        val isUserCorrect = selectedOption == question.correctIndex
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUserCorrect) SuccessGreen.copy(alpha = 0.08f) else WarningAmber.copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isUserCorrect) SuccessGreen.copy(alpha = 0.3f) else WarningAmber.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isUserCorrect) Icons.Filled.CheckCircle else Icons.Filled.Info,
                                        contentDescription = "Feedback Icon",
                                        tint = if (isUserCorrect) SuccessGreenLight else WarningAmber,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isUserCorrect) "CORRECT ANSWER" else "CONCEPTUAL CLARIFICATION",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUserCorrect) SuccessGreenLight else WarningAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = question.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Bottom Action Panel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        if (!isSubmitted) {
                            Button(
                                onClick = onSubmit,
                                enabled = selectedOption != null,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_answer_button")
                            ) {
                                Text(text = "SUBMIT RESPONSE", fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                            }
                        } else {
                            val isLast = currentIndex + 1 == questions.size
                            Button(
                                onClick = onNext,
                                colors = ButtonDefaults.buttonColors(containerColor = if (isLast) SuccessGreen else PrimaryBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("next_question_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isLast) "SEE SCORE REPORT" else "NEXT QUESTION MODULE",
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (isLast) Icons.Filled.Check else Icons.Filled.ChevronRight,
                                        contentDescription = "Forward Icon"
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SecondaryBlue)
                }
            }
        }
    }
}
