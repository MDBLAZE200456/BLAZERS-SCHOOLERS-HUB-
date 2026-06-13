package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.*
import com.example.ui.components.CodeBox
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    course: Course,
    modules: List<Module>,
    progressList: List<CourseProgressEntity>,
    quizScore: QuizScoreEntity?,
    isBookmarked: (String) -> Boolean,
    isCourseDownloaded: Boolean,
    downloadedModuleIds: Set<String>,
    lastActiveModuleId: String?,
    onToggleModule: (String, Boolean) -> Unit,
    onToggleBookmark: (Module) -> Unit,
    onModuleViewed: (String) -> Unit,
    onDownloadCourse: () -> Unit,
    onRemoveCourseDownload: () -> Unit,
    onToggleModuleDownload: (String) -> Unit,
    onLaunchQuiz: () -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Learn", "Code Snippets", "Practice Quiz")

    val completedModuleIds = remember(progressList) {
        progressList.filter { it.completed }.map { it.moduleId }.toSet()
    }

    LaunchedEffect(courseId) {
        if (modules.isNotEmpty()) {
            val startModuleId = lastActiveModuleId ?: modules.first().id
            onModuleViewed(startModuleId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = course.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = course.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepSlateSurface,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = DeepSlateBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Course Meta header info band
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            DifficultyBadge(difficulty = course.difficulty)
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = WarningAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${course.rating} Rating",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Duration: ${course.durationText}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Course-level Download Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCourseDownloaded) Icons.Filled.OnlinePrediction else Icons.Filled.CloudDownload,
                                contentDescription = "Download Info",
                                tint = if (isCourseDownloaded) Color(0xFF4ADE80) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCourseDownloaded) "Full Course Downloaded" else "Offline Copy Missing",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCourseDownloaded) Color(0xFF4ADE80) else Color(0xFF94A3B8)
                            )
                        }
                        
                        Button(
                            onClick = {
                                if (isCourseDownloaded) onRemoveCourseDownload() else onDownloadCourse()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCourseDownloaded) Color(0xFF7F1D1D).copy(alpha = 0.5f) else Color(0xFF2563EB),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCourseDownloaded) Icons.Filled.DeleteOutline else Icons.Filled.Download,
                                    contentDescription = "Action",
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isCourseDownloaded) "Clear Cache" else "Download Course",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Tabs row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF0F172A),
                contentColor = SecondaryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SecondaryBlue
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        selectedContentColor = SecondaryBlue,
                        unselectedContentColor = Color(0xFF64748B),
                        modifier = Modifier.testTag("course_tab_$index")
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> LearnTabContent(
                        modules = modules,
                        completedModuleIds = completedModuleIds,
                        downloadedModuleIds = downloadedModuleIds,
                        lastActiveModuleId = lastActiveModuleId,
                        onCompletedChange = { moduleId, completed ->
                            onToggleModule(moduleId, completed)
                        },
                        onToggleDownload = onToggleModuleDownload,
                        onModuleViewed = onModuleViewed
                    )
                    1 -> SnippetsTabContent(
                        modules = modules,
                        isBookmarked = isBookmarked,
                        downloadedModuleIds = downloadedModuleIds,
                        onToggleBookmark = onToggleBookmark
                    )
                    2 -> QuizTabContent(
                        course = course,
                        quizScore = quizScore,
                        isCourseDownloaded = isCourseDownloaded,
                        onLaunchQuiz = onLaunchQuiz
                    )
                }
            }
        }
    }
}

@Composable
fun LearnTabContent(
    modules: List<Module>,
    completedModuleIds: Set<String>,
    downloadedModuleIds: Set<String>,
    lastActiveModuleId: String?,
    onCompletedChange: (String, Boolean) -> Unit,
    onToggleDownload: (String) -> Unit,
    onModuleViewed: (String) -> Unit
) {
    if (modules.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No learn material found.", color = Color(0xFF94A3B8))
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { module ->
                val isCompleted = completedModuleIds.contains(module.id)
                val isSavedOffline = downloadedModuleIds.contains(module.id)
                val isLastActive = module.id == lastActiveModuleId
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        if (isLastActive) 1.5.dp else 1.dp,
                        if (isLastActive) WarningAmber
                        else if (isCompleted) SuccessGreen.copy(alpha = 0.5f)
                        else Color(0xFF334155)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModuleViewed(module.id) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "MODULE ${module.orderIndex}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SecondaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))

                                if (isLastActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(WarningAmber.copy(alpha = 0.15f))
                                            .border(1.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RESUMING POINT",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = WarningAmber,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                
                                // Beautiful single module Download button/indicator
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSavedOffline) Color(0xFF15803D).copy(alpha = 0.2f)
                                            else Color(0xFF0F172A)
                                        )
                                        .clickable { onToggleDownload(module.id) }
                                        .testTag("download_module_${module.id}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isSavedOffline) Icons.Filled.CloudDone else Icons.Filled.CloudDownload,
                                        contentDescription = "Download module",
                                        tint = if (isSavedOffline) Color(0xFF4ADE80) else Color(0xFF475569),
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                
                                if (isSavedOffline) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OFFLINE",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF4ADE80),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Clean checkbox / Checkmark Toggle layout
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onCompletedChange(module.id, !isCompleted) }
                                    .testTag("toggle_module_${module.id}")
                            ) {
                                Text(
                                    text = if (isCompleted) "COMPLETED" else "MARK DONE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) SuccessGreenLight else Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                    contentDescription = "Complete Toggle",
                                    tint = if (isCompleted) SuccessGreenLight else Color(0xFF475569),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val formattedBody = module.learnContent.replace("**", "")
                        Text(
                            text = formattedBody,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCBD5E1),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SnippetsTabContent(
    modules: List<Module>,
    isBookmarked: (String) -> Boolean,
    downloadedModuleIds: Set<String>,
    onToggleBookmark: (Module) -> Unit
) {
    if (modules.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No code snippets found.", color = Color(0xFF94A3B8))
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { module ->
                val bookmarked = isBookmarked(module.id)
                val isSavedOffline = downloadedModuleIds.contains(module.id)
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = module.snippetTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (isSavedOffline) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF15803D).copy(alpha = 0.2f))
                                            .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "OFFLINE SAVED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF4ADE80),
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            // Bookmark and Star Toggle Layout
                            IconButton(
                                onClick = { onToggleBookmark(module) },
                                modifier = Modifier.testTag("bookmark_module_${module.id}")
                            ) {
                                Icon(
                                    imageVector = if (bookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Bookmark Code",
                                    tint = if (bookmarked) WarningAmber else Color(0xFF64748B)
                                )
                            }
                        }

                        Text(
                            text = module.snippetExplanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        val language = if (module.courseId == "c-prog") "c" else if (module.courseId.startsWith("java")) "java" else "python"
                        CodeBox(
                            code = module.snippetCode,
                            language = language,
                            title = module.snippetTitle,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuizTabContent(
    course: Course,
    quizScore: QuizScoreEntity?,
    isCourseDownloaded: Boolean,
    onLaunchQuiz: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BatchPrediction,
                        contentDescription = "Quiz",
                        tint = SecondaryBlue,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Interactive Practice Quiz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Consolidate your local systems knowledge by completing multiple-choice checkpoints.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                if (isCourseDownloaded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF15803D).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "QUIZ QUESTIONS FULLY DOWNLOADED FOR OFFLINE USE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4ADE80),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // If user already took the quiz, present historic score card
                if (quizScore != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "HIGHEST SCORE HISTORY:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${quizScore.score} / ${quizScore.maxScore} correct",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SuccessGreenLight
                                )
                            }
                            
                            val percentage = (quizScore.score.toFloat() / quizScore.maxScore * 100).toInt()
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = if (percentage >= 80) SuccessGreenLight else WarningAmber
                            )
                        }
                    }
                }

                Button(
                    onClick = onLaunchQuiz,
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("launch_quiz_button")
                ) {
                    Text(
                        text = if (quizScore != null) "RETAKE PRACTICE QUIZ" else "START CHALLENGE",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyBadge(difficulty: String) {
    val color = when (difficulty.lowercase()) {
        "core" -> Color(0xFF38BDF8)
        "advanced" -> Color(0xFFFB923C)
        else -> Color(0xFFC084FC)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = difficulty.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 9.sp
        )
    }
}
