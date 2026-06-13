package com.example.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.*
import com.example.ui.screens.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle

object ScreenRoutes {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val COURSE_DETAIL = "course/{courseId}"
    const val QUIZ = "quiz/{courseId}"
    const val BOOKMARKS = "bookmarks"

    fun courseDetailPath(courseId: String) = "course/$courseId"
    fun quizPath(courseId: String) = "quiz/$courseId"
}

@Composable
fun AppNavHost(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val isLoggedIn = remember { viewModel.isUserLoggedInOnLaunch() }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) ScreenRoutes.DASHBOARD else ScreenRoutes.LOGIN,
        modifier = modifier
    ) {
        // Screen 1: Login/Onboarding Screen
        composable(ScreenRoutes.LOGIN) {
            val savedStudents by viewModel.savedStudents.collectAsStateWithLifecycle()
            LoginScreen(
                savedStudents = savedStudents,
                onLoginClick = { username, pin, firstName, surname, lastNameOptional, phoneNumber, email, department ->
                    viewModel.loginStudent(
                        username = username,
                        pin = pin,
                        firstName = firstName,
                        surname = surname,
                        lastNameOptional = lastNameOptional,
                        phoneNumber = phoneNumber,
                        email = email,
                        department = department
                    ) {
                        navController.navigate(ScreenRoutes.DASHBOARD) {
                            popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Screen 2: Main Dashboard Screen
        composable(ScreenRoutes.DASHBOARD) {
            val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
            val courseProgress by viewModel.courseProgress.collectAsStateWithLifecycle()
            val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
            val offlineLogs by viewModel.offlineLogs.collectAsStateWithLifecycle()
            val courseDownloads by viewModel.courseDownloads.collectAsStateWithLifecycle()
            val moduleDownloads by viewModel.moduleDownloads.collectAsStateWithLifecycle()
            val savedStudents by viewModel.savedStudents.collectAsStateWithLifecycle()
            val bookmarkedSnippets by viewModel.bookmarkedSnippets.collectAsStateWithLifecycle()
            val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
            val resumeSession by viewModel.resumeSession.collectAsStateWithLifecycle()
            val activeTheme by viewModel.themeMode.collectAsStateWithLifecycle()

            DashboardScreen(
                userProgress = userProgress,
                courseProgress = courseProgress,
                isOfflineMode = isOfflineMode,
                offlineLogs = offlineLogs,
                courseDownloads = courseDownloads,
                moduleDownloads = moduleDownloads,
                savedStudents = savedStudents,
                bookmarkedSnippetsCount = bookmarkedSnippets.size,
                quizScores = quizScores,
                resumeSession = resumeSession,
                activeTheme = activeTheme,
                onThemeChange = { mode -> viewModel.updateThemeMode(mode) },
                onToggleOfflineMode = { viewModel.toggleOfflineMode() },
                onDownloadCourse = { courseId -> viewModel.downloadCourse(courseId) },
                onRemoveCourseDownload = { courseId -> viewModel.removeDownloadedCourse(courseId) },
                onCourseClick = { courseId ->
                    navController.navigate(ScreenRoutes.courseDetailPath(courseId))
                },
                onResumeQuizClick = { courseId, index, score ->
                    viewModel.resumeQuiz(courseId, index, score)
                    navController.navigate(ScreenRoutes.quizPath(courseId))
                },
                onNavigateToBookmarks = {
                    navController.navigate(ScreenRoutes.BOOKMARKS)
                },
                onLogoutClick = {
                    viewModel.logoutStudent()
                    navController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(ScreenRoutes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }

        // Screen 3: Course Module Detail Screen
        composable(
            route = ScreenRoutes.COURSE_DETAIL,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val course = remember(courseId) { viewModel.getCourseDetails(courseId) }
            val modules = remember(courseId) { viewModel.getCourseModules(courseId) }
            val progressList by viewModel.courseProgress.collectAsStateWithLifecycle()
            val quizScores by viewModel.quizScores.collectAsStateWithLifecycle()
            val bookmarks by viewModel.bookmarkedSnippets.collectAsStateWithLifecycle()
            val courseDownloads by viewModel.courseDownloads.collectAsStateWithLifecycle()
            val moduleDownloads by viewModel.moduleDownloads.collectAsStateWithLifecycle()
            val resumeSession by viewModel.resumeSession.collectAsStateWithLifecycle()

            val courseProgress = remember(progressList, courseId) {
                progressList.filter { it.courseId == courseId }
            }
            val quizScore = remember(quizScores, courseId) {
                quizScores.find { it.courseId == courseId }
            }
            val lastActiveModuleId = remember(resumeSession) {
                if (resumeSession?.lastCourseId == courseId) resumeSession?.lastModuleId else null
            }

            if (course != null) {
                CourseDetailScreen(
                    courseId = courseId,
                    course = course,
                    modules = modules,
                    progressList = courseProgress,
                    quizScore = quizScore,
                    isBookmarked = { moduleId -> bookmarks.any { it.id == moduleId } },
                    isCourseDownloaded = courseDownloads.any { it.courseId == courseId },
                    downloadedModuleIds = remember(moduleDownloads, courseId) {
                        moduleDownloads.filter { it.courseId == courseId }.map { it.moduleId }.toSet()
                    },
                    lastActiveModuleId = lastActiveModuleId,
                    onToggleModule = { moduleId, completed ->
                        viewModel.toggleModuleCompleted(courseId, moduleId, completed)
                    },
                    onToggleBookmark = { module ->
                        viewModel.toggleBookmark(module)
                    },
                    onModuleViewed = { moduleId ->
                        viewModel.updateLastActiveLesson(courseId, moduleId)
                    },
                    onDownloadCourse = { viewModel.downloadCourse(courseId) },
                    onRemoveCourseDownload = { viewModel.removeDownloadedCourse(courseId) },
                    onToggleModuleDownload = { moduleId -> viewModel.toggleModuleDownload(courseId, moduleId) },
                    onLaunchQuiz = {
                        viewModel.startQuizForCourse(courseId)
                        navController.navigate(ScreenRoutes.quizPath(courseId))
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Screen 4: Interactive Quiz Screen
        composable(
            route = ScreenRoutes.QUIZ,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val questions by viewModel.currentQuizQuestions.collectAsStateWithLifecycle()
            val currentIndex by viewModel.currentQuestionIndex.collectAsStateWithLifecycle()
            val selectedOption by viewModel.selectedOptionIndex.collectAsStateWithLifecycle()
            val isSubmitted by viewModel.isAnswerSubmitted.collectAsStateWithLifecycle()
            val scoreCount by viewModel.quizScoreCount.collectAsStateWithLifecycle()
            val isFinished by viewModel.quizFinished.collectAsStateWithLifecycle()

            QuizScreen(
                questions = questions,
                currentIndex = currentIndex,
                selectedOption = selectedOption,
                isSubmitted = isSubmitted,
                scoreCount = scoreCount,
                isFinished = isFinished,
                onOptionSelect = { optionIndex ->
                    viewModel.selectQuizOption(optionIndex)
                },
                onSubmit = {
                    viewModel.submitQuizAnswer()
                },
                onNext = {
                    viewModel.nextQuizQuestion()
                },
                onBackToCourse = {
                    navController.popBackStack()
                }
            )
        }

        // Screen 5: Bookmark/Code Vault
        composable(ScreenRoutes.BOOKMARKS) {
            val bookmarks by viewModel.bookmarkedSnippets.collectAsStateWithLifecycle()

            BookmarkScreen(
                bookmarks = bookmarks,
                onRemoveBookmark = { bookmarkId ->
                    viewModel.deleteBookmark(bookmarkId)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
