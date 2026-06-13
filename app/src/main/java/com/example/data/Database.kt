package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// 1. Entities
@Entity(tableName = "course_progress")
data class CourseProgressEntity(
    @PrimaryKey val id: String, // format: "username_courseId_moduleId"
    val username: String,
    val courseId: String,
    val moduleId: String,
    val completed: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_downloads")
data class CourseDownloadEntity(
    @PrimaryKey val id: String, // format: "username_courseId"
    val username: String,
    val courseId: String,
    val downloadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "module_downloads")
data class ModuleDownloadEntity(
    @PrimaryKey val id: String, // format: "username_moduleId"
    val username: String,
    val courseId: String,
    val moduleId: String,
    val downloadTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey val id: String, // format: "username_courseId"
    val username: String,
    val courseId: String,
    val score: Int,
    val maxScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "code_bookmarks")
data class CodeBookmarkEntity(
    @PrimaryKey val id: String, // format: "username_moduleId"
    val username: String,
    val courseId: String,
    val title: String,
    val description: String,
    val code: String,
    val language: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "student_profiles")
data class StudentProfileEntity(
    @PrimaryKey val username: String,
    val securityPin: String = "",
    val streakCount: Int = 0,
    val xp: Int = 0,
    val lastActiveDate: String = "", // format: "YYYY-MM-DD"
    val lastLoginTimestamp: Long = System.currentTimeMillis(),
    val firstName: String = "",
    val surname: String = "",
    val lastNameOptional: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val department: String = ""
)

@Entity(tableName = "user_resume_sessions")
data class UserResumeSessionEntity(
    @PrimaryKey val username: String,
    val lastCourseId: String = "",
    val lastModuleId: String = "",
    val quizCourseId: String = "",
    val quizCurrentIndex: Int = 0,
    val quizScoreCount: Int = 0,
    val quizIsActive: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

// 2. Data Access Object
@Dao
interface ScholarDao {
    // Progress Flow
    @Query("SELECT * FROM course_progress WHERE username = :username")
    fun getCourseProgressFlow(username: String): Flow<List<CourseProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: CourseProgressEntity)

    // Quiz Scores
    @Query("SELECT * FROM quiz_scores WHERE username = :username")
    fun getAllQuizScoresFlow(username: String): Flow<List<QuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizScore(score: QuizScoreEntity)

    // User Resume Sessions
    @Query("SELECT * FROM user_resume_sessions WHERE username = :username")
    fun getResumeSessionFlow(username: String): Flow<UserResumeSessionEntity?>

    @Query("SELECT * FROM user_resume_sessions WHERE username = :username")
    suspend fun getResumeSession(username: String): UserResumeSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResumeSession(session: UserResumeSessionEntity)

    // Code Bookmarks
    @Query("SELECT * FROM code_bookmarks WHERE username = :username ORDER BY timestamp DESC")
    fun getAllBookmarksFlow(username: String): Flow<List<CodeBookmarkEntity>>

    @Query("SELECT * FROM code_bookmarks WHERE username = :username AND id = :id")
    suspend fun getBookmarkById(username: String, id: String): CodeBookmarkEntity?

    @Query("SELECT * FROM code_bookmarks WHERE username = :username AND id = :id")
    fun getBookmarkFlow(username: String, id: String): Flow<CodeBookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: CodeBookmarkEntity)

    @Query("DELETE FROM code_bookmarks WHERE username = :username AND id = :id")
    suspend fun deleteBookmarkById(username: String, id: String)

    // Student Profiles Flow
    @Query("SELECT * FROM student_profiles ORDER BY lastLoginTimestamp DESC")
    fun getAllStudentsFlow(): Flow<List<StudentProfileEntity>>

    @Query("SELECT * FROM student_profiles WHERE username = :username")
    suspend fun getStudentByUsername(username: String): StudentProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentProfileEntity)

    // Downloads
    @Query("SELECT * FROM course_downloads WHERE username = :username")
    fun getCourseDownloadsFlow(username: String): Flow<List<CourseDownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseDownload(download: CourseDownloadEntity)

    @Query("DELETE FROM course_downloads WHERE username = :username AND courseId = :courseId")
    suspend fun deleteCourseDownload(username: String, courseId: String)

    @Query("SELECT * FROM module_downloads WHERE username = :username")
    fun getModuleDownloadsFlow(username: String): Flow<List<ModuleDownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModuleDownload(download: ModuleDownloadEntity)

    @Query("DELETE FROM module_downloads WHERE username = :username AND moduleId = :moduleId")
    suspend fun deleteModuleDownload(username: String, moduleId: String)
}

// 3. Database Abstract Class
@Database(
    entities = [
        CourseProgressEntity::class,
        QuizScoreEntity::class,
        CodeBookmarkEntity::class,
        StudentProfileEntity::class,
        CourseDownloadEntity::class,
        ModuleDownloadEntity::class,
        UserResumeSessionEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class ScholarDatabase : RoomDatabase() {
    abstract fun dao(): ScholarDao
}
