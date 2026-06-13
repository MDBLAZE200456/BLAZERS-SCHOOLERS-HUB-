package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.AppThemeMode
import com.example.ui.theme.*
import kotlinx.coroutines.launch

// Data model representing a local course item with progress
data class CourseCardItem(
    val id: String,
    val title: String,
    val iconName: String,
    val modulesCount: Int,
    val difficultyTag: String,
    val description: String,
    val completedCount: Int,
    val progressPercent: Float,
    val isDownloaded: Boolean
)

// Dynamic badge model
data class ScholarBadge(
    val title: String,
    val criteria: String,
    val icon: ImageVector,
    val tint: Color,
    val isEarned: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userProgress: StudentProfileEntity,
    courseProgress: List<CourseProgressEntity>,
    isOfflineMode: Boolean,
    offlineLogs: List<String>,
    courseDownloads: List<CourseDownloadEntity>,
    moduleDownloads: List<ModuleDownloadEntity>,
    savedStudents: List<StudentProfileEntity>,
    bookmarkedSnippetsCount: Int,
    quizScores: List<QuizScoreEntity>,
    resumeSession: UserResumeSessionEntity?,
    activeTheme: AppThemeMode,
    onThemeChange: (AppThemeMode) -> Unit,
    onToggleOfflineMode: () -> Unit,
    onDownloadCourse: (String) -> Unit,
    onRemoveCourseDownload: (String) -> Unit,
    onCourseClick: (String) -> Unit,
    onResumeQuizClick: (String, Int, Int) -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val courses = remember { com.example.data.Curriculums.courses }
    val totalModulesCount = remember { com.example.data.Curriculums.modules.size }

    // Map course progress maps
    val progressMap = remember(courseProgress) {
        courseProgress.filter { it.completed }.groupBy { it.courseId }
    }

    // Build the dynamic courses lists
    val courseCardItems = remember(courses, progressMap, courseDownloads) {
        courses.map { course ->
            val modulesInCourse = com.example.data.Curriculums.modules.filter { it.courseId == course.id }.size
            val completedCount = progressMap[course.id]?.size ?: 0
            val progressPercent = if (modulesInCourse > 0) completedCount.toFloat() / modulesInCourse else 0f
            val isDownloaded = courseDownloads.any { it.courseId == course.id }
            CourseCardItem(
                id = course.id,
                title = course.title,
                iconName = course.iconName,
                modulesCount = modulesInCourse,
                difficultyTag = course.difficulty,
                description = course.description,
                completedCount = completedCount,
                progressPercent = progressPercent,
                isDownloaded = isDownloaded
            )
        }
    }

    // Compute dynamic badges earned
    val completedModuleIds = remember(courseProgress) {
        courseProgress.filter { it.completed }.map { it.moduleId }.toSet()
    }
    
    val perfectQuizScores = remember(quizScores) {
        quizScores.filter { it.score == it.maxScore && it.maxScore > 0 }.size
    }

    var selectedBadgeForDetail by remember { mutableStateOf<ScholarBadge?>(null) }
    var selectedStudentForDetail by remember { mutableStateOf<StudentProfileEntity?>(null) }

    // Dynamic Leaderboard list sorted by XP descending
    val leaderboardList = remember(savedStudents, userProgress) {
        // Ensure active student is in the list
        val activeStudentInSaved = savedStudents.any { it.username == userProgress.username }
        val fullList = if (!activeStudentInSaved && userProgress.username.isNotEmpty()) {
            savedStudents + userProgress
        } else {
            savedStudents
        }
        fullList.sortedByDescending { it.xp }
    }

    val studentBadges = remember(userProgress, completedModuleIds, bookmarkedSnippetsCount, perfectQuizScores, leaderboardList) {
        listOf(
            ScholarBadge(
                title = "C Maverick",
                criteria = "Pass 3+ C Modules",
                icon = Icons.Filled.Terminal,
                tint = Color(0xFF38BDF8),
                isEarned = completedModuleIds.filter { it.startsWith("c-") }.size >= 3
            ),
            ScholarBadge(
                title = "OOP Knight",
                criteria = "Pass 3+ Java Modules",
                icon = Icons.Filled.Coffee,
                tint = Color(0xFFFFB74D),
                isEarned = completedModuleIds.filter { it.startsWith("j1-") }.size >= 3
            ),
            ScholarBadge(
                title = "Streak Star",
                criteria = "Reach 5+ days Active Streak",
                icon = Icons.Filled.LocalFireDepartment,
                tint = Color(0xFFFF5252),
                isEarned = userProgress.streakCount >= 5
            ),
            ScholarBadge(
                title = "Perfect Score",
                criteria = "100% on any practice quiz",
                icon = Icons.Filled.Star,
                tint = Color(0xFF4ADE80),
                isEarned = perfectQuizScores > 0
            ),
            ScholarBadge(
                title = "XP Titan",
                criteria = "Earn more than 1,000 Total XP",
                icon = Icons.Filled.ElectricBolt,
                tint = Color(0xFFFFE082),
                isEarned = userProgress.xp >= 1000
            ),
            ScholarBadge(
                title = "Snippet Vault",
                criteria = "Bookmark 1+ Code snippet",
                icon = Icons.Filled.Bookmark,
                tint = Color(0xFFC084FC),
                isEarned = bookmarkedSnippetsCount >= 1
            ),
            ScholarBadge(
                title = "Course Conqueror",
                criteria = "Complete 100% of any course",
                icon = Icons.Filled.School,
                tint = Color(0xFF10B981),
                isEarned = courses.any { course ->
                    val courseModules = com.example.data.Curriculums.modules.filter { it.courseId == course.id }
                    courseModules.isNotEmpty() && courseModules.all { completedModuleIds.contains(it.id) }
                }
            ),
            ScholarBadge(
                title = "Apex Scholar",
                criteria = "Reach Rank #1 on Leaderboard",
                icon = Icons.Filled.MilitaryTech,
                tint = Color(0xFFFBBF24),
                isEarned = leaderboardList.firstOrNull()?.username == userProgress.username
            ),
            ScholarBadge(
                title = "High Performer",
                criteria = "Score 80%+ on any quiz",
                icon = Icons.Filled.WorkspacePremium,
                tint = Color(0xFF06B6D4),
                isEarned = quizScores.any { it.maxScore > 0 && (it.score.toFloat() / it.maxScore) >= 0.8f }
            ),
            ScholarBadge(
                title = "Active Scholar",
                criteria = "Pass your first lesson module",
                icon = Icons.Filled.MenuBook,
                tint = Color(0xFF60A5FA),
                isEarned = completedModuleIds.isNotEmpty()
            )
        )
    }

    val gridState = rememberLazyGridState()
    var showTrackMenu by remember { mutableStateOf(false) }
    var showThemeMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 1 }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = showScrollToTop,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    },
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("scroll_to_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowUpward,
                        contentDescription = "Scroll to top"
                    )
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.ic_app_logo),
                            contentDescription = "Blazers Scholar Hub Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF60A5FA).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text(
                                text = "Core Learning Station",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                actions = {
                    // 1. Navigation Menu for Modules
                    Box {
                        IconButton(
                            onClick = { showTrackMenu = true },
                            modifier = Modifier.testTag("modules_nav_dropdown_trigger")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LibraryBooks,
                                contentDescription = "Switch learning modules",
                                tint = Color(0xFF60A5FA)
                            )
                        }
                        DropdownMenu(
                            expanded = showTrackMenu,
                            onDismissRequest = { showTrackMenu = false },
                            modifier = Modifier.background(DeepSlateSurface)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = { Icon(imageVector = Icons.Filled.Terminal, contentDescription = "C", tint = Color(0xFF38BDF8)) },
                                text = { Text("C Programming Module", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showTrackMenu = false
                                    onCourseClick("c-prog")
                                },
                                modifier = Modifier.testTag("nav_module_c_prog")
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(imageVector = Icons.Filled.Coffee, contentDescription = "Java", tint = Color(0xFFFFB74D)) },
                                text = { Text("Java Programming Module", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showTrackMenu = false
                                    onCourseClick("java-1")
                                },
                                modifier = Modifier.testTag("nav_module_java_1")
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(imageVector = Icons.Filled.Code, contentDescription = "Python", tint = Color(0xFF4ADE80)) },
                                text = { Text("Python Programming Module", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showTrackMenu = false
                                    onCourseClick("python")
                                },
                                modifier = Modifier.testTag("nav_module_python")
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(imageVector = Icons.Filled.Psychology, contentDescription = "AI/ML", tint = Color(0xFFC084FC)) },
                                text = { Text("AI & Machine Learning Module", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showTrackMenu = false
                                    onCourseClick("ai-ml")
                                },
                                modifier = Modifier.testTag("nav_module_ai_ml")
                            )
                            DropdownMenuItem(
                                leadingIcon = { Icon(imageVector = Icons.Filled.GridView, contentDescription = "Math", tint = Color(0xFFEF4444)) },
                                text = { Text("Discrete Math Module", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    showTrackMenu = false
                                    onCourseClick("discrete-ds")
                                },
                                modifier = Modifier.testTag("nav_module_discrete_ds")
                            )
                        }
                    }

                    // 2. Hybrid / Offline Status Button
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isOfflineMode) Color(0xFF15803D).copy(alpha = 0.2f)
                                else Color(0xFF1E293B)
                            )
                            .border(
                                1.dp,
                                if (isOfflineMode) Color(0xFF22C55E).copy(alpha = 0.4f)
                                else Color(0xFF334155),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onToggleOfflineMode() }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isOfflineMode) Icons.Filled.WifiOff else Icons.Filled.Wifi,
                                contentDescription = "Offline Status Indicator",
                                tint = if (isOfflineMode) Color(0xFF4ADE80) else Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isOfflineMode) "OFFLINE" else "HYBRID",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfflineMode) Color(0xFF4ADE80) else Color(0xFF94A3B8)
                            )
                        }
                    }

                    // 3. Bookmarks Button
                    IconButton(
                        onClick = onNavigateToBookmarks,
                        modifier = Modifier.testTag("vault_nav_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmark,
                            contentDescription = "Code Bookmark Vault",
                            tint = WarningAmber
                        )
                    }

                    // 4. Custom Theme Selector visible at the Top Right corner of the screen
                    Box {
                        IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("top_theme_select_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = "Theme style selection",
                                tint = when (activeTheme) {
                                    AppThemeMode.BLACK -> Color(0xFF38BDF8)
                                    AppThemeMode.WHITE -> Color(0xFF2563EB)
                                    AppThemeMode.RAINBOW -> Color(0xFFF43F5E)
                                }
                            )
                        }
                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false },
                            modifier = Modifier.background(DeepSlateSurface)
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                },
                                text = { Text("Black Theme 🌑", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    onThemeChange(AppThemeMode.BLACK)
                                    showThemeMenu = false
                                },
                                modifier = Modifier.testTag("theme_menu_black")
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(1.dp, Color.Gray, CircleShape)
                                    )
                                },
                                text = { Text("White Theme ☀️", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    onThemeChange(AppThemeMode.WHITE)
                                    showThemeMenu = false
                                },
                                modifier = Modifier.testTag("theme_menu_white")
                            )
                            DropdownMenuItem(
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color(0xFFE11D48),
                                                        Color(0xFFA855F7),
                                                        Color(0xFF22D3EE)
                                                    )
                                                )
                                            )
                                    )
                                },
                                text = { Text("Rainbow Theme 🌈", style = MaterialTheme.typography.bodyMedium) },
                                onClick = {
                                    onThemeChange(AppThemeMode.RAINBOW)
                                    showThemeMenu = false
                                },
                                modifier = Modifier.testTag("theme_menu_rainbow")
                            )
                        }
                    }

                    // 5. Logout Button
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Log Out",
                            tint = Color(0xFFEF4444)
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // Section 1: Dynamic Student Profile Summary
            item(span = { GridItemSpan(2) }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AccentSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_profile_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreenLight)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = userProgress.username.ifEmpty { "Guest Student" },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                val userLevel = (userProgress.xp / 150) + 1
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF2563EB).copy(alpha = 0.2f))
                                        .border(1.dp, Color(0xFF2563EB), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                        .testTag("user_level_badge")
                                ) {
                                    Text(
                                        text = "LVL $userLevel",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SecondaryBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ROLE: SYSTEMS & FOUNDATIONAL ENGINEER",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row {
                                Column(modifier = Modifier.padding(end = 24.dp)) {
                                    Text(text = "TOTAL POINTS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${userProgress.xp} XP",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = SuccessGreenLight
                                    )
                                }
                                Column {
                                    Text(text = "MODULES PASSED", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${courseProgress.filter { it.completed }.size} / $totalModulesCount",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Black,
                                        color = SecondaryBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            
                            val xpNeededForNextLevel = 150
                            val currentLevelXp = userProgress.xp % xpNeededForNextLevel
                            val levelProgress = currentLevelXp.toFloat() / xpNeededForNextLevel.toFloat()
                            val nextLevel = (userProgress.xp / 150) + 2
                            
                            Column(modifier = Modifier.fillMaxWidth().padding(end = 12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LEVEL PROGRESS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$currentLevelXp / $xpNeededForNextLevel XP to Lvl $nextLevel",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { levelProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .testTag("user_level_progress_bar"),
                                    color = Color(0xFF2563EB),
                                    trackColor = Color(0xFF1E293B)
                                )
                            }
                        }

                        // Super vibrant streak count badge
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFFF9F43),
                                            Color(0xFFFF5252)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Streak Fire",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "${userProgress.streakCount} DAYS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    if (userProgress.firstName.isNotBlank() || userProgress.surname.isNotBlank() || userProgress.email.isNotBlank() || userProgress.phoneNumber.isNotBlank() || userProgress.department.isNotBlank()) {
                        Divider(
                            color = AccentSlate.copy(alpha = 0.5f), 
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Text(
                                text = "STUDENT SECURE CREDENTIAL CARD 🪪",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val fullName = buildString {
                                        append(userProgress.firstName)
                                        if (userProgress.surname.isNotBlank()) append(" ").append(userProgress.surname)
                                        if (userProgress.lastNameOptional.isNotBlank()) append(" ").append(userProgress.lastNameOptional)
                                    }
                                    ProfileInfoRow(label = "FULL NAME", value = fullName, icon = Icons.Filled.Person)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ProfileInfoRow(label = "DEPARTMENT", value = userProgress.department.ifEmpty { "General Engineering" }, icon = Icons.Filled.School)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    ProfileInfoRow(label = "PHONE NUMBER", value = userProgress.phoneNumber.ifEmpty { "N/A" }, icon = Icons.Filled.Phone)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ProfileInfoRow(label = "EMAIL ADDRESS", value = userProgress.email.ifEmpty { "N/A" }, icon = Icons.Filled.Email)
                                }
                            }
                        }
                    }
                }
            }
        }

            // Theme Customization Controls
            item(span = { GridItemSpan(2) }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AccentSlate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_customizer_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "AESTHETIC STUDY LOUNGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = "Personalize your layout for long study sessions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.Palette,
                                contentDescription = "Theme Color Settings",
                                tint = SecondaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // --- Black Button ---
                            Button(
                                onClick = { onThemeChange(AppThemeMode.BLACK) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0F172A),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (activeTheme == AppThemeMode.BLACK) 2.dp else 1.dp,
                                    color = if (activeTheme == AppThemeMode.BLACK) Color(0xFF38BDF8) else Color(0xFF334155)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_btn_black")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.Black)
                                            .border(1.dp, Color.White, CircleShape)
                                    )
                                    Text(
                                        text = "Black",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }

                            // --- White Button ---
                            Button(
                                onClick = { onThemeChange(AppThemeMode.WHITE) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF0F172A)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = if (activeTheme == AppThemeMode.WHITE) 2.dp else 1.dp,
                                    color = if (activeTheme == AppThemeMode.WHITE) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_btn_white")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                            .border(1.dp, Color.LightGray, CircleShape)
                                    )
                                    Text(
                                        text = "White",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A)
                                    )
                                }
                            }

                            // --- Rainbow Button ---
                            Button(
                                onClick = { onThemeChange(AppThemeMode.RAINBOW) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_btn_rainbow")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFFE11D48),
                                                    Color(0xFFD946EF),
                                                    Color(0xFF3B82F6),
                                                    Color(0xFF10B981)
                                                )
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            width = if (activeTheme == AppThemeMode.RAINBOW) 2.dp else 0.dp,
                                            color = if (activeTheme == AppThemeMode.RAINBOW) Color.White else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Rainbow",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Resume Learning Journey Section
            val hasLastActiveCourse = !resumeSession?.lastCourseId.isNullOrEmpty()
            val hasActiveQuiz = resumeSession?.quizIsActive == true && !resumeSession.quizCourseId.isNullOrEmpty()

            if (hasLastActiveCourse || hasActiveQuiz) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "RESUME YOUR LEARNING",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (hasLastActiveCourse) {
                                val lastCourse = remember(resumeSession) {
                                    courses.find { it.id == resumeSession?.lastCourseId }
                                }
                                val lastModule = remember(resumeSession) {
                                    com.example.data.Curriculums.modules.find { it.id == resumeSession?.lastModuleId }
                                }
                                
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onCourseClick(resumeSession?.lastCourseId ?: "") }
                                        .testTag("resume_lesson_card")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.MenuBook,
                                                contentDescription = "Lesson Icon",
                                                tint = WarningAmber,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "CONTINUE LESSON",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = WarningAmber
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = lastCourse?.title ?: "System Programming",
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = lastModule?.title ?: "Module Intro",
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { onCourseClick(resumeSession?.lastCourseId ?: "") },
                                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = "CONTINUE",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
                                        }
                                    }
                                }
                            }

                            if (hasActiveQuiz) {
                                val quizCourse = remember(resumeSession) {
                                    courses.find { it.id == resumeSession?.quizCourseId }
                                }
                                val quizQuestionsCount = remember(resumeSession) {
                                    com.example.data.Curriculums.quizzes.filter { it.courseId == resumeSession?.quizCourseId }.size
                                }
                                
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, SecondaryBlue.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { 
                                            onResumeQuizClick(
                                                resumeSession?.quizCourseId ?: "",
                                                resumeSession?.quizCurrentIndex ?: 0,
                                                resumeSession?.quizScoreCount ?: 0
                                            )
                                        }
                                        .testTag("resume_quiz_card")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Extension,
                                                contentDescription = "Quiz Icon",
                                                tint = SecondaryBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "ACTIVE CHALLENGE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = SecondaryBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = quizCourse?.title ?: "Practice Quiz",
                                            style = MaterialTheme.typography.titleSmall,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Question ${(resumeSession?.quizCurrentIndex ?: 0) + 1} of $quizQuestionsCount",
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { 
                                                onResumeQuizClick(
                                                    resumeSession?.quizCourseId ?: "",
                                                    resumeSession?.quizCurrentIndex ?: 0,
                                                    resumeSession?.quizScoreCount ?: 0
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryBlue),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = "RESUME QUIZ",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Gamified Badges Shelf (cabinet)
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "BADGES CABINET (${studentBadges.count { it.isEarned }} / ${studentBadges.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Badges Chest",
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Display Badge grid cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        studentBadges.forEach { badge ->
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (badge.isEarned) DeepSlateSurface else Color(0xFF131D31).copy(alpha = 0.5f)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(
                                    width = 1.2.dp,
                                    color = if (badge.isEarned) badge.tint.copy(alpha = 0.7f) else Color(0xFF1E293B)
                                ),
                                modifier = Modifier
                                    .width(136.dp)
                                    .clickable { selectedBadgeForDetail = badge }
                                    .testTag("badge_card_${badge.title.replace(" ", "_").lowercase()}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (badge.isEarned) badge.tint.copy(alpha = 0.15f)
                                                else Color(0xFF1E293B)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (badge.isEarned) badge.icon else Icons.Filled.Lock,
                                            contentDescription = badge.title,
                                            tint = if (badge.isEarned) badge.tint else Color(0xFF475569),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = badge.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (badge.isEarned) Color.White else Color(0xFF64748B),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    
                                    Spacer(modifier = Modifier.height(2.dp))
                                    
                                    Text(
                                        text = badge.criteria,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF94A3B8),
                                        textAlign = TextAlign.Center,
                                        fontSize = 9.sp,
                                        lineHeight = 11.sp,
                                        minLines = 2,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Friendly Leaderboard Panel
            item(span = { GridItemSpan(2) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "COMPETITIVE LEADERBOARD",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = "Rank List",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("leaderboard_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            leaderboardList.forEachIndexed { rank, student ->
                                val isMe = student.username == userProgress.username
                                val badgeBg = when (rank) {
                                    0 -> Color(0xFFFFD700).copy(alpha = 0.15f) // Gold
                                    1 -> Color(0xFFC0C0C0).copy(alpha = 0.15f) // Silver
                                    2 -> Color(0xFFCD7F32).copy(alpha = 0.15f) // Bronze
                                    else -> Color.Transparent
                                }
                                val badgeText = when (rank) {
                                    0 -> Color(0xFFFFD700)
                                    1 -> Color(0xFFE2E8F0)
                                    2 -> Color(0xFFCD7F32)
                                    else -> Color(0xFF64748B)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedStudentForDetail = student }
                                        .background(if (isMe) Color(0xFF3B82F6).copy(alpha = 0.1f) else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isMe) Color(0xFF3B82F6).copy(alpha = 0.3f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Rank visual circle
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(badgeBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (rank == 0) {
                                                Text(text = "👑", fontSize = 11.sp)
                                            } else {
                                                Text(
                                                    text = "${rank + 1}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = badgeText
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = student.username,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isMe) FontWeight.Black else FontWeight.Bold,
                                                    color = if (isMe) SecondaryBlue else Color.White
                                                )
                                                if (isMe) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(Color(0xFF2563EB))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "YOU",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 8.sp,
                                                            color = Color.White
                                                        )
                                                    }
                                                }
                                            }
                                            
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.LocalFireDepartment,
                                                    contentDescription = "Streak",
                                                    tint = Color(0xFFFF9F43),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "${student.streakCount} day streak",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "${student.xp} XP",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Black,
                                        color = if (isMe) Color(0xFF4ADE80) else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Offline Database Hub & Download management logs
            item(span = { GridItemSpan(2) }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isOfflineMode) Color(0xFF15803D) else Color(0xFF1E293B)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offline_sync_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isOfflineMode) Color(0xFF22C55E) else Color(0xFF3B82F6))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OFFLINE DATABASE HUB",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 1.sp
                                )
                            }
                            
                            Button(
                                onClick = onToggleOfflineMode,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isOfflineMode) Color(0xFF15803D).copy(alpha = 0.3f) else Color(0xFF1E293B),
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, if (isOfflineMode) Color(0xFF22C55E) else Color(0xFF334155)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (isOfflineMode) "Forced Offline" else "Switch to Hybrid",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Download statistics summary
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "OFFLINE DOWNLOADS STATUS",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${courseDownloads.size} Courses • ${moduleDownloads.size} Lessons Saved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Filled.DownloadDone,
                                contentDescription = "Saves",
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isOfflineMode) 
                                "All activities, completed modules, quiz records, and bookmark vaults are saved locally in the SQLite 'scholar.db' database, allowing complete offline usage."
                            else 
                                "Applet is running in hybrid simulation mode. Sync status with cloud endpoints is simulated over local persistence caches.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8),
                            lineHeight = 15.sp
                        )

                        // If user has downloaded courses, let them delete them here as download management
                        if (courseDownloads.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Manage Saved Offline Storage:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                courseDownloads.forEach { download ->
                                    val matchCourse = courses.find { it.id == download.courseId }
                                    if (matchCourse != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF1E293B))
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Filled.LibraryBooks,
                                                    contentDescription = "Book",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = matchCourse.title,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            
                                            // Action bin icon to remove downloaded materials
                                            IconButton(
                                                onClick = { onRemoveCourseDownload(download.courseId) },
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .testTag("delete_download_${download.courseId}")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Delete,
                                                    contentDescription = "Uninstall",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "SQLite Transaction Logs (Real-time):",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                                .heightIn(max = 120.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (offlineLogs.isEmpty()) {
                                  Text(
                                      text = "No local SQLite logs available yet. Toggle modules or quizzes to write updates.",
                                      style = MaterialTheme.typography.bodySmall,
                                      color = Color(0xFF475569)
                                  )
                            } else {
                                offlineLogs.forEach { log ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "•",
                                            color = if (isOfflineMode) Color(0xFF22C55E) else Color(0xFF3B82F6),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = log,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (log.contains("error", ignoreCase = true)) Color(0xFFEF4444) else Color(0xFFCBD5E1),
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 5: Academic Curriculums Title
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACADEMIC CURRICULUMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${courses.size} COURSES ACTIVE",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryBlue
                    )
                }
            }

            // Section 6: Grid of Courses list
            items(courseCardItems) { cardItem ->
                CourseGridCard(
                    cardItem = cardItem,
                    onCourseClick = onCourseClick,
                    onDownloadCourse = onDownloadCourse,
                    onRemoveCourseDownload = onRemoveCourseDownload
                )
            }
        }

        // --- INTERACTIVE DIALOGS ---
        if (selectedBadgeForDetail != null) {
            BadgeDetailDialog(
                badge = selectedBadgeForDetail!!,
                onDismiss = { selectedBadgeForDetail = null }
            )
        }

        if (selectedStudentForDetail != null) {
            val totalModulesCount = remember { com.example.data.Curriculums.modules.size }
            val completedModuleIds = remember(courseProgress) {
                courseProgress.filter { it.completed }.map { it.moduleId }.toSet()
            }
            LeaderboardDetailDialog(
                student = selectedStudentForDetail!!,
                rank = leaderboardList.indexOfFirst { it.username == selectedStudentForDetail!!.username },
                isMe = selectedStudentForDetail!!.username == userProgress.username,
                myProgress = userProgress,
                completedCount = if (selectedStudentForDetail!!.username == userProgress.username) {
                    completedModuleIds.size
                } else {
                    (selectedStudentForDetail!!.xp / 50).coerceAtMost(totalModulesCount)
                },
                totalModulesCount = totalModulesCount,
                badges = remember(selectedStudentForDetail, completedModuleIds, quizScores, bookmarkedSnippetsCount, leaderboardList) {
                    val s = selectedStudentForDetail!!
                    if (s.username == userProgress.username) {
                        studentBadges
                    } else {
                        val sCompletedIds = if (s.xp >= 1500) {
                            completedModuleIds + setOf("c-mod-1", "c-mod-2", "c-mod-3", "j1-mod-1", "j1-mod-2", "j1-mod-3")
                        } else if (s.xp >= 700) {
                            setOf("j1-mod-1", "j1-mod-2", "j1-mod-3")
                        } else {
                            emptySet()
                        }
                        val sScores = if (s.xp >= 1500) {
                            listOf(QuizScoreEntity("id", s.username, "c-prog", 5, 5))
                        } else {
                            emptyList()
                        }
                        val sBookmarksCount = if (s.xp >= 1000) 1 else 0
                        
                        listOf(
                            ScholarBadge("C Maverick", "Pass 3+ C Modules", Icons.Filled.Terminal, Color(0xFF38BDF8), sCompletedIds.filter { it.startsWith("c-") }.size >= 3),
                            ScholarBadge("OOP Knight", "Pass 3+ Java Modules", Icons.Filled.Coffee, Color(0xFFFFB74D), sCompletedIds.filter { it.startsWith("j1-") }.size >= 3),
                            ScholarBadge("Streak Star", "Reach 5+ days Active Streak", Icons.Filled.LocalFireDepartment, Color(0xFFFF5252), s.streakCount >= 5),
                            ScholarBadge("Perfect Score", "100% on any practice quiz", Icons.Filled.Star, Color(0xFF4ADE80), sScores.isNotEmpty()),
                            ScholarBadge("XP Titan", "Earn more than 1,000 Total XP", Icons.Filled.ElectricBolt, Color(0xFFFFE082), s.xp >= 1000),
                            ScholarBadge("Snippet Vault", "Bookmark 1+ Code snippet", Icons.Filled.Bookmark, Color(0xFFC084FC), sBookmarksCount >= 1),
                            ScholarBadge("Course Conqueror", "Complete 100% of any course", Icons.Filled.School, Color(0xFF10B981), s.xp >= 1500),
                            ScholarBadge("Apex Scholar", "Reach Rank #1 on Leaderboard", Icons.Filled.MilitaryTech, Color(0xFFFBBF24), s.xp >= 1800),
                            ScholarBadge("High Performer", "Score 80%+ on any quiz", Icons.Filled.WorkspacePremium, Color(0xFF06B6D4), s.xp >= 700),
                            ScholarBadge("Active Scholar", "Pass your first lesson module", Icons.Filled.MenuBook, Color(0xFF60A5FA), s.xp >= 150)
                        )
                    }
                },
                onDismiss = { selectedStudentForDetail = null }
            )
        }
    }
}

@Composable
fun CourseGridCard(
    cardItem: CourseCardItem,
    onCourseClick: (String) -> Unit,
    onDownloadCourse: (String) -> Unit,
    onRemoveCourseDownload: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
        border = BorderStroke(1.dp, AccentSlate),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCourseClick(cardItem.id) }
            .testTag("course_card_${cardItem.id}")
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
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCourseIcon(cardItem.iconName),
                        contentDescription = cardItem.title,
                        tint = when (cardItem.difficultyTag.lowercase()) {
                            "core" -> Color(0xFF38BDF8)
                            "advanced" -> Color(0xFFFB923C)
                            else -> Color(0xFFC084FC)
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Download icon button directly on each card!
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (cardItem.isDownloaded) Color(0xFF15803D).copy(alpha = 0.2f)
                            else Color(0xFF0F172A)
                        )
                        .clickable {
                            if (cardItem.isDownloaded) {
                                onRemoveCourseDownload(cardItem.id)
                            } else {
                                onDownloadCourse(cardItem.id)
                            }
                        }
                        .testTag("download_button_${cardItem.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (cardItem.isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download,
                        contentDescription = "Download Toggle",
                        tint = if (cardItem.isDownloaded) Color(0xFF4ADE80) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Course Title
            Text(
                text = cardItem.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Course Description
            Text(
                text = cardItem.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF94A3B8),
                maxLines = 2,
                minLines = 2,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Offline indicator text label
            if (cardItem.isDownloaded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AVAILABLE OFFLINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4ADE80),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Progress Indicators
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${cardItem.completedCount}/${cardItem.modulesCount} Mods",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCBD5E1),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${(cardItem.progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (cardItem.progressPercent == 1f) Color(0xFF4ADE80) else Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .height(5.dp)
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(Color(0xFF0F172A))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(cardItem.progressPercent)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = if (cardItem.progressPercent == 1f) {
                                        listOf(Color(0xFF4ADE80), Color(0xFF22C55E))
                                    } else {
                                        listOf(Color(0xFF38BDF8), Color(0xFF2563EB))
                                    }
                                )
                            )
                    )
                }
            }
        }
    }
}

private fun getCourseIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "terminal" -> Icons.Filled.Terminal
        "coffee" -> Icons.Filled.Coffee
        "code" -> Icons.Filled.Code
        "psychology" -> Icons.Filled.Psychology
        "grid_view" -> Icons.Filled.GridView
        "window" -> Icons.Filled.WebAsset
        else -> Icons.Filled.Computer
    }
}

@Composable
fun BadgeDetailDialog(
    badge: ScholarBadge,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("badge_dialog_ok_button")
            ) {
                Text("GOT IT", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (badge.isEarned) badge.tint.copy(alpha = 0.15f) else Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (badge.isEarned) badge.icon else Icons.Filled.Lock,
                        contentDescription = badge.title,
                        tint = if (badge.isEarned) badge.tint else Color(0xFF475569),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = badge.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "STATUS: ${if (badge.isEarned) "🏆 UNLOCKED" else "🔒 LOCKED"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isEarned) Color(0xFF4ADE80) else Color(0xFFEF4444)
                )
                Text(
                    text = "CRITERIA: ${badge.criteria}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (badge.isEarned) {
                        "Congratulations! You've successfully master-classed this educational milestone."
                    } else {
                        "Keep expanding your system knowledge! Meet the requirement to unlock this prestigious achievement and climb the competitive rankings."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        },
        containerColor = DeepSlateSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun LeaderboardDetailDialog(
    student: StudentProfileEntity,
    rank: Int,
    isMe: Boolean,
    myProgress: StudentProfileEntity,
    completedCount: Int,
    totalModulesCount: Int,
    badges: List<ScholarBadge>,
    onDismiss: () -> Unit
) {
    val level = (student.xp / 150) + 1
    val currentLevelXp = student.xp % 150
    val progressPercent = currentLevelXp.toFloat() / 150f
    
    val rankLabel = when (rank) {
        0 -> "👑 GOLD PODIUM"
        1 -> "🥈 SILVER PODIUM"
        2 -> "🥉 BRONZE PODIUM"
        else -> "🏅 DIVISION RANK #${rank + 1}"
    }
    
    val rankColor = when (rank) {
        0 -> Color(0xFFFFD700)
        1 -> Color(0xFFE2E8F0)
        2 -> Color(0xFFCD7F32)
        else -> Color(0xFF38BDF8)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("leaderboard_dialog_close_button")
            ) {
                Text("CLOSE PROFILE", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, 
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(rankColor.copy(alpha = 0.3f), rankColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.username.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = student.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = rankLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = rankColor,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("SCHOLAR LEVEL", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Text("Lvl $level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("CUMULATIVE POINTS", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Text("${student.xp} XP", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = rankColor,
                            trackColor = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentLevelXp / 150 XP for Next Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("STREAK", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFFF9F43),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${student.streakCount} Days", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("MODULES", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B))
                            Text("$completedCount / $totalModulesCount", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                if (!isMe) {
                    val pointsDiff = (student.xp - myProgress.xp)
                    val textComparison = when {
                        pointsDiff > 0 -> "You are $pointsDiff XP behind ${student.username}! Finish quizzes to catch up!"
                        pointsDiff < 0 -> "You lead ${student.username} by ${-pointsDiff} XP. Excel in your modules to stay ahead!"
                        else -> "You are tied in points with ${student.username}! Next lesson decides the leader."
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningAmber.copy(alpha = 0.15f))
                            .border(1.dp, WarningAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = textComparison,
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessGreen.copy(alpha = 0.15f))
                            .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "This is your public scholar rank page. Share high scores on division topics to defend your champion podium!",
                            style = MaterialTheme.typography.bodySmall,
                            color = SuccessGreenLight,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(
                    text = "EARNED ACHIEVEMENTS (${badges.count { it.isEarned }} / ${badges.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    badges.forEach { b ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (b.isEarned) Color(0xFF0F172A) else Color(0xFF0F172A).copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (b.isEarned) b.tint.copy(alpha = 0.5f) else Color(0xFF1E293B)
                            ),
                            modifier = Modifier.width(100.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (b.isEarned) b.tint.copy(alpha = 0.1f) else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (b.isEarned) b.icon else Icons.Filled.Lock,
                                        contentDescription = b.title,
                                        tint = if (b.isEarned) b.tint else Color(0xFF475569),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = b.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (b.isEarned) Color.White else Color(0xFF64748B),
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = DeepSlateSurface,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun ProfileInfoRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF60A5FA),
            modifier = Modifier.size(14.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
