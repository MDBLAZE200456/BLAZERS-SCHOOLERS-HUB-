package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class MainViewModel(
    private val repository: ScholarRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _themeMode = MutableStateFlow(
        try {
            AppThemeMode.valueOf(authRepository.getTheme())
        } catch (e: Exception) {
            AppThemeMode.BLACK
        }
    )
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    fun updateThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        authRepository.setTheme(mode.name)
        addOfflineLog("Aesthetic theme style customized to ${mode.name}")
    }

    private val _currentStudentUsername = MutableStateFlow(
        if (authRepository.isLoggedIn()) authRepository.getLoggedInUsername() ?: "" else ""
    )
    val currentStudentUsername: StateFlow<String> = _currentStudentUsername.asStateFlow()

    private val _isOfflineMode = MutableStateFlow(true)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    private val _offlineLogs = MutableStateFlow<List<String>>(
        listOf("Offline SQLite database connected.", "Ready to learn offline without requiring cellular or internet connection.")
    )
    val offlineLogs: StateFlow<List<String>> = _offlineLogs.asStateFlow()

    fun toggleOfflineMode() {
        val nextMode = !_isOfflineMode.value
        _isOfflineMode.value = nextMode
        addOfflineLog("Offline status toggled. Mode: ${if (nextMode) "FORCED OFFLINE" else "HYBRID LOCAL-SYNC"}")
    }

    fun addOfflineLog(message: String) {
        val timestamp = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
        _offlineLogs.value = listOf("[$timestamp] $message") + _offlineLogs.value.take(19)
    }

    // 1. Reactive Streams from DB mapped to the active student session
    val savedStudents: StateFlow<List<StudentProfileEntity>> = repository.getAllStudentsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProgress: StateFlow<StudentProfileEntity> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(StudentProfileEntity(username = ""))
            else repository.getStudentProfileFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentProfileEntity(username = ""))

    @OptIn(ExperimentalCoroutinesApi::class)
    val courseProgress: StateFlow<List<CourseProgressEntity>> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else repository.getProgressFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarkedSnippets: StateFlow<List<CodeBookmarkEntity>> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else repository.getBookmarksFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val quizScores: StateFlow<List<QuizScoreEntity>> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else repository.getAllQuizScoresFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val courseDownloads: StateFlow<List<CourseDownloadEntity>> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else repository.getCourseDownloadsFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val moduleDownloads: StateFlow<List<ModuleDownloadEntity>> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(emptyList())
            else repository.getModuleDownloadsFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val resumeSession: StateFlow<UserResumeSessionEntity?> = _currentStudentUsername
        .flatMapLatest { username ->
            if (username.isEmpty()) flowOf(null)
            else repository.getResumeSessionFlow(username)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 2. Local App UI Quiz Engine States
    private val _currentQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuizQuestions = _currentQuizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex = _selectedOptionIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted = _isAnswerSubmitted.asStateFlow()

    private val _quizScoreCount = MutableStateFlow(0)
    val quizScoreCount = _quizScoreCount.asStateFlow()

    private val _quizFinished = MutableStateFlow(false)
    val quizFinished = _quizFinished.asStateFlow()

    private val _activeCourseIdForQuiz = MutableStateFlow<String?>(null)
    val activeCourseIdForQuiz = _activeCourseIdForQuiz.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureInitialStudentsExist()
        }
    }

    // 3. User operations
    fun loginStudent(
        username: String,
        pin: String,
        firstName: String = "",
        surname: String = "",
        lastNameOptional: String = "",
        phoneNumber: String = "",
        email: String = "",
        department: String = "",
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val studentProfile = repository.authenticateOrCreateStudent(
                username = username,
                pin = pin,
                firstName = firstName,
                surname = surname,
                lastNameOptional = lastNameOptional,
                phoneNumber = phoneNumber,
                email = email,
                department = department
            )
            _currentStudentUsername.value = studentProfile.username
            authRepository.login(studentProfile.username) // Persist login state
            _isOfflineMode.value = true // Force offline mode enabled after loging
            _offlineLogs.value = listOf(
                "Offline SQLite database connected securely.",
                "Offline Mode dynamically enforced upon login.",
                "Student session: '${studentProfile.username}' active under Local Persistence Model."
            )
            onComplete()
        }
    }

    fun logoutStudent() {
        authRepository.logout() // Clear persistent login state
        _currentStudentUsername.value = ""
    }

    fun isUserLoggedInOnLaunch(): Boolean {
        return authRepository.isLoggedIn()
    }

    fun toggleModuleCompleted(courseId: String, moduleId: String, completed: Boolean) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.toggleModuleProgress(username, courseId, moduleId, completed)
                addOfflineLog("Progress sync: module '$moduleId' ${if (completed) "marked COMPLETED" else "marked INCOMPLETE"}. Written to Room SQLite.")
            }
        }
    }

    fun toggleBookmark(module: Module) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.toggleBookmark(
                    username = username,
                    id = module.id,
                    courseId = module.courseId,
                    title = module.snippetTitle,
                    description = module.title,
                    code = module.snippetCode,
                    language = if (module.courseId == "c-prog") "c" else if (module.courseId.startsWith("java")) "java" else "python"
                )
                addOfflineLog("Bookmark toggled: '${module.snippetTitle}' saved to local Room SQLite cache successfully.")
            }
        }
    }

    fun deleteBookmark(bookmarkId: String) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                val isBookmarkedFlow = repository.isBookmarkedFlow(username, bookmarkId).first()
                if (isBookmarkedFlow) {
                    val bookmarked = bookmarkedSnippets.value.find { it.id == "${username}_${bookmarkId}" || it.id == bookmarkId }
                    if (bookmarked != null) {
                        repository.toggleBookmark(
                            username = username,
                            id = bookmarkId,
                            courseId = bookmarked.courseId,
                            title = bookmarked.title,
                            description = bookmarked.description,
                            code = bookmarked.code,
                            language = bookmarked.language
                        )
                        addOfflineLog("Bookmark removed: '${bookmarked.title}' deleted from offline DB.")
                    }
                }
            }
        }
    }

    // 4. Multiple Choice Quiz Engine Loops
    fun startQuizForCourse(courseId: String) {
        val questions = repository.getQuizzesForCourse(courseId)
        _activeCourseIdForQuiz.value = courseId
        _currentQuizQuestions.value = questions
        _currentQuestionIndex.value = 0
        _selectedOptionIndex.value = null
        _isAnswerSubmitted.value = false
        _quizScoreCount.value = 0
        _quizFinished.value = false

        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateLastActiveQuizSession(username, courseId, 0, 0, true)
                addOfflineLog("Started a new practice quiz for course '$courseId'. Saved to Room SQLite.")
            }
        }
    }

    fun selectQuizOption(index: Int) {
        if (!_isAnswerSubmitted.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun submitQuizAnswer() {
        val currentQuestions = _currentQuizQuestions.value
        val currentIndex = _currentQuestionIndex.value
        val selectedIndex = _selectedOptionIndex.value
        
        if (selectedIndex != null && !_isAnswerSubmitted.value && currentIndex < currentQuestions.size) {
            _isAnswerSubmitted.value = true
            if (selectedIndex == currentQuestions[currentIndex].correctIndex) {
                _quizScoreCount.value += 1
            }
            
            val username = _currentStudentUsername.value
            val courseId = _activeCourseIdForQuiz.value ?: ""
            if (username.isNotEmpty() && courseId.isNotEmpty()) {
                viewModelScope.launch {
                    repository.updateLastActiveQuizSession(username, courseId, currentIndex, _quizScoreCount.value, true)
                    addOfflineLog("Quiz progress preserved: response registered for Q${currentIndex+1} with score ${quizScoreCount.value}")
                }
            }
        }
    }

    fun nextQuizQuestion() {
        val currentQuestions = _currentQuizQuestions.value
        val currentIndex = _currentQuestionIndex.value
        
        if (currentIndex + 1 < currentQuestions.size) {
            _currentQuestionIndex.value += 1
            _selectedOptionIndex.value = null
            _isAnswerSubmitted.value = false

            val username = _currentStudentUsername.value
            val courseId = _activeCourseIdForQuiz.value ?: ""
            if (username.isNotEmpty() && courseId.isNotEmpty()) {
                viewModelScope.launch {
                    repository.updateLastActiveQuizSession(username, courseId, _currentQuestionIndex.value, _quizScoreCount.value, true)
                    addOfflineLog("Moved to Q${_currentQuestionIndex.value + 1}. Progress saved.")
                }
            }
        } else {
            // End of quiz reached
            _quizFinished.value = true
            val courseId = _activeCourseIdForQuiz.value ?: return
            val totalScore = _quizScoreCount.value
            val maxGrading = currentQuestions.size
            val username = _currentStudentUsername.value
            if (username.isNotEmpty()) {
                viewModelScope.launch {
                    repository.saveQuizScore(username, courseId, totalScore, maxGrading)
                    repository.updateLastActiveQuizSession(username, courseId, 0, 0, false)
                    addOfflineLog("Saved Practice Quiz Score: $totalScore / $maxGrading for course $courseId to SQL.")
                }
            }
        }
    }

    fun resumeQuiz(courseId: String, savedIndex: Int, savedScore: Int) {
        val questions = repository.getQuizzesForCourse(courseId)
        _activeCourseIdForQuiz.value = courseId
        _currentQuizQuestions.value = questions
        _currentQuestionIndex.value = savedIndex
        _selectedOptionIndex.value = null
        _isAnswerSubmitted.value = false
        _quizScoreCount.value = savedScore
        _quizFinished.value = false
        addOfflineLog("Quiz progress resumed: Course '$courseId' starting from Q${savedIndex + 1} with score $savedScore.")
    }

    fun updateLastActiveLesson(courseId: String, moduleId: String) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.updateLastActiveLessonSession(username, courseId, moduleId)
                addOfflineLog("Resumable state synced: Lesson position of student marked at '$moduleId' under Course '$courseId'.")
            }
        }
    }

    fun getCourseModules(courseId: String): List<Module> {
        return repository.getModulesForCourse(courseId)
    }

    fun getCourseDetails(courseId: String): Course? {
        return repository.getCourse(courseId)
    }

    // Download flow handlers with transaction logs
    fun downloadCourse(courseId: String) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.downloadCourse(username, courseId)
                addOfflineLog("Offline Storage: Course '$courseId' with all lessons & code snippets downloaded.")
            }
        }
    }

    fun removeDownloadedCourse(courseId: String) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                repository.removeDownloadedCourse(username, courseId)
                addOfflineLog("Cache Cleanup: Entire course '$courseId' removed from local device.")
            }
        }
    }

    fun toggleModuleDownload(courseId: String, moduleId: String) {
        val username = _currentStudentUsername.value
        if (username.isNotEmpty()) {
            viewModelScope.launch {
                val downloaded = moduleDownloads.value.any { it.moduleId == moduleId }
                if (downloaded) {
                    repository.removeDownloadedModule(username, moduleId)
                    addOfflineLog("Cache Cleanup: Lesson/Module '$moduleId' deleted from local offline store.")
                } else {
                    repository.downloadModule(username, courseId, moduleId)
                    addOfflineLog("Offline Storage: Lesson/Module '$moduleId' saved for offline study.")
                }
            }
        }
    }
}

enum class AppThemeMode {
    BLACK,
    WHITE,
    RAINBOW
}

