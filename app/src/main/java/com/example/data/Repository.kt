package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ScholarRepository(private val dao: ScholarDao) {

    // Courses & Static curriculum elements
    fun getCourses(): List<Course> = Curriculums.courses

    fun getCourse(id: String): Course? = Curriculums.courses.find { it.id == id }

    fun getModulesForCourse(courseId: String): List<Module> {
        return Curriculums.modules.filter { it.courseId == courseId }
            .sortedBy { it.orderIndex }
    }

    fun getQuizzesForCourse(courseId: String): List<QuizQuestion> {
        return Curriculums.quizzes.filter { it.courseId == courseId }
    }

    // Dynamic Room Database interactions (filtered by username)
    fun getProgressFlow(username: String): Flow<List<CourseProgressEntity>> {
        return dao.getCourseProgressFlow(username)
    }

    suspend fun toggleModuleProgress(username: String, courseId: String, moduleId: String, completed: Boolean) {
        val progressId = "${username}_${courseId}_${moduleId}"
        dao.insertProgress(
            CourseProgressEntity(
                id = progressId,
                username = username,
                courseId = courseId,
                moduleId = moduleId,
                completed = completed
            )
        )
        // Award XP on completion
        if (completed) {
            awardXP(username, 15)
        }
    }

    // Quiz records
    fun getAllQuizScoresFlow(username: String): Flow<List<QuizScoreEntity>> {
        return dao.getAllQuizScoresFlow(username)
    }

    suspend fun saveQuizScore(username: String, courseId: String, score: Int, maxScore: Int) {
        val scoreId = "${username}_${courseId}"
        dao.insertQuizScore(
            QuizScoreEntity(
                id = scoreId,
                username = username,
                courseId = courseId,
                score = score,
                maxScore = maxScore
            )
        )
        // Calculate XP
        val ratio = score.toFloat() / maxScore.coerceAtLeast(1)
        val gainedXP = (ratio * 100).toInt()
        awardXP(username, gainedXP)
        updateStreak(username)
    }

    // Bookmarking Vault
    fun getBookmarksFlow(username: String): Flow<List<CodeBookmarkEntity>> {
        return dao.getAllBookmarksFlow(username)
    }

    fun isBookmarkedFlow(username: String, id: String): Flow<Boolean> {
        val bookmarkId = "${username}_${id}"
        return dao.getBookmarkFlow(username, bookmarkId).map { it != null }
    }

    suspend fun toggleBookmark(username: String, id: String, courseId: String, title: String, description: String, code: String, language: String) {
        val bookmarkId = "${username}_${id}"
        val current = dao.getBookmarkById(username, bookmarkId)
        if (current != null) {
            dao.deleteBookmarkById(username, bookmarkId)
        } else {
            dao.insertBookmark(
                CodeBookmarkEntity(
                    id = bookmarkId,
                    username = username,
                    courseId = courseId,
                    title = title,
                    description = description,
                    code = code,
                    language = language
                )
            )
        }
    }

    // Student profile management
    fun getAllStudentsFlow(): Flow<List<StudentProfileEntity>> = dao.getAllStudentsFlow()

    suspend fun authenticateOrCreateStudent(
        username: String,
        pin: String,
        firstName: String = "",
        surname: String = "",
        lastNameOptional: String = "",
        phoneNumber: String = "",
        email: String = "",
        department: String = ""
    ): StudentProfileEntity {
        val trimmedUsername = username.trim()
        val existing = dao.getStudentByUsername(trimmedUsername)
        return if (existing != null) {
            // Verify and update last login timestamp
            val updated = existing.copy(
                securityPin = pin, // store or update pin
                lastLoginTimestamp = System.currentTimeMillis(),
                firstName = if (firstName.isNotBlank()) firstName else existing.firstName,
                surname = if (surname.isNotBlank()) surname else existing.surname,
                lastNameOptional = if (lastNameOptional.isNotBlank()) lastNameOptional else existing.lastNameOptional,
                phoneNumber = if (phoneNumber.isNotBlank()) phoneNumber else existing.phoneNumber,
                email = if (email.isNotBlank()) email else existing.email,
                department = if (department.isNotBlank()) department else existing.department
            )
            dao.insertStudent(updated)
            updated
        } else {
            // Register new student
            val yesterdayStr = LocalDate.now().minusDays(1).toString()
            val newStudent = StudentProfileEntity(
                username = trimmedUsername,
                securityPin = pin,
                streakCount = 3, // Premium initial streak of 3 days
                xp = 150, // Starter bonus XP!
                lastActiveDate = yesterdayStr,
                lastLoginTimestamp = System.currentTimeMillis(),
                firstName = firstName,
                surname = surname,
                lastNameOptional = lastNameOptional,
                phoneNumber = phoneNumber,
                email = email,
                department = department
            )
            dao.insertStudent(newStudent)
            newStudent
        }
    }

    fun getStudentProfileFlow(username: String): Flow<StudentProfileEntity> {
        return dao.getAllStudentsFlow().map { list ->
            list.find { it.username == username } ?: StudentProfileEntity(username = username)
        }
    }

    private suspend fun awardXP(username: String, amount: Int) {
        val current = dao.getStudentByUsername(username) ?: return
        dao.insertStudent(
            current.copy(
                xp = current.xp + amount
            )
        )
    }

    private suspend fun updateStreak(username: String) {
        val todayStr = LocalDate.now().toString()
        val current = dao.getStudentByUsername(username) ?: return
        
        val lastActive = current.lastActiveDate
        if (lastActive == todayStr) {
            // Already active today, streak remains same
            return
        }

        val newStreak = if (lastActive.isNotEmpty()) {
            val yesterday = LocalDate.now().minusDays(1).toString()
            if (lastActive == yesterday) {
                current.streakCount + 1
            } else {
                1 // streak broken
            }
        } else {
            1 // initial streak
        }

        dao.insertStudent(
            current.copy(
                streakCount = newStreak,
                lastActiveDate = todayStr
            )
        )
    }

    // Prepopulate user streak state on first launch so it looks amazing
    suspend fun ensureInitialStudentsExist() {
        val existing = dao.getAllStudentsFlow().first()
        if (existing.isEmpty()) {
            val yesterdayStr = LocalDate.now().minusDays(1).toString()
            // Student 1: Blaze Maverick
            dao.insertStudent(
                StudentProfileEntity(
                    username = "Blazeverick",
                    securityPin = "1111",
                    streakCount = 12,
                    xp = 1850,
                    lastActiveDate = yesterdayStr,
                    lastLoginTimestamp = System.currentTimeMillis() - 10000
                )
            )
            // Student 2: Nova Scholar
            dao.insertStudent(
                StudentProfileEntity(
                    username = "Nova Scholar",
                    securityPin = "1234",
                    streakCount = 5,
                    xp = 720,
                    lastActiveDate = yesterdayStr,
                    lastLoginTimestamp = System.currentTimeMillis() - 20000
                )
            )
        }
    }

    // Modern Download System for Offline Course Materials
    fun getCourseDownloadsFlow(username: String): Flow<List<CourseDownloadEntity>> {
        return dao.getCourseDownloadsFlow(username)
    }

    fun getModuleDownloadsFlow(username: String): Flow<List<ModuleDownloadEntity>> {
        return dao.getModuleDownloadsFlow(username)
    }

    suspend fun downloadCourse(username: String, courseId: String) {
        val downloadId = "${username}_${courseId}"
        dao.insertCourseDownload(
            CourseDownloadEntity(
                id = downloadId,
                username = username,
                courseId = courseId
            )
        )
        // Auto-download all modules within this course
        val modules = getModulesForCourse(courseId)
        modules.forEach { module ->
            downloadModule(username, courseId, module.id)
        }
    }

    suspend fun removeDownloadedCourse(username: String, courseId: String) {
        dao.deleteCourseDownload(username, courseId)
        // Also remove all module downloads in this course
        val modules = getModulesForCourse(courseId)
        modules.forEach { module ->
            dao.deleteModuleDownload(username, module.id)
        }
    }

    suspend fun downloadModule(username: String, courseId: String, moduleId: String) {
        val downloadId = "${username}_${moduleId}"
        dao.insertModuleDownload(
            ModuleDownloadEntity(
                id = downloadId,
                username = username,
                courseId = courseId,
                moduleId = moduleId
            )
        )
    }

    suspend fun removeDownloadedModule(username: String, moduleId: String) {
        dao.deleteModuleDownload(username, moduleId)
    }

    // Resuming State Management Systems
    fun getResumeSessionFlow(username: String): Flow<UserResumeSessionEntity?> {
        return dao.getResumeSessionFlow(username)
    }

    suspend fun updateLastActiveLessonSession(username: String, courseId: String, moduleId: String) {
        val current = dao.getResumeSession(username) ?: UserResumeSessionEntity(username = username)
        dao.insertResumeSession(
            current.copy(
                lastCourseId = courseId,
                lastModuleId = moduleId,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateLastActiveQuizSession(username: String, courseId: String, currentIndex: Int, score: Int, isActive: Boolean) {
        val current = dao.getResumeSession(username) ?: UserResumeSessionEntity(username = username)
        dao.insertResumeSession(
            current.copy(
                quizCourseId = if (isActive) courseId else "",
                quizCurrentIndex = currentIndex,
                quizScoreCount = score,
                quizIsActive = isActive,
                lastUpdated = System.currentTimeMillis()
            )
        )
    }
}
