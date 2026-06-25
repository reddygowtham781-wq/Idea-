package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// --- Entities ---

@Entity(tableName = "ideas")
data class IdeaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // "Tech", "Business", "Creative", "Social", "Other"
    val status: String,   // "Drafting", "Active", "Completed", "Paused"
    val targetAudience: String,
    val valueProposition: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = false,
    val authorName: String = "Anonymous",
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false
)

@Entity(tableName = "work_steps")
data class WorkStepEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ideaId: Int,
    val title: String,
    val notes: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ideaId: Int,
    val description: String,
    val impact: String, // "Minor", "Major", "Blocker"
    val isSolved: Boolean = false,
    val solution: String? = null,
    val aiAdvice: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ideaId: Int,
    val authorName: String,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)

// --- DAOs ---

@Dao
interface IdeaDao {
    @Query("SELECT * FROM ideas ORDER BY createdAt DESC")
    fun getAllIdeas(): Flow<List<IdeaEntity>>

    @Query("SELECT * FROM ideas WHERE id = :id LIMIT 1")
    suspend fun getIdeaById(id: Int): IdeaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: IdeaEntity): Long

    @Update
    suspend fun updateIdea(idea: IdeaEntity)

    @Query("DELETE FROM ideas WHERE id = :id")
    suspend fun deleteIdeaById(id: Int)
}

@Dao
interface WorkStepDao {
    @Query("SELECT * FROM work_steps WHERE ideaId = :ideaId ORDER BY timestamp ASC")
    fun getStepsForIdea(ideaId: Int): Flow<List<WorkStepEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: WorkStepEntity)

    @Update
    suspend fun updateStep(step: WorkStepEntity)

    @Query("DELETE FROM work_steps WHERE id = :id")
    suspend fun deleteStepById(id: Int)

    @Query("DELETE FROM work_steps WHERE ideaId = :ideaId")
    suspend fun deleteStepsForIdea(ideaId: Int)
}

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistakes WHERE ideaId = :ideaId ORDER BY timestamp DESC")
    fun getMistakesForIdea(ideaId: Int): Flow<List<MistakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeEntity): Long

    @Update
    suspend fun updateMistake(mistake: MistakeEntity)

    @Query("DELETE FROM mistakes WHERE id = :id")
    suspend fun deleteMistakeById(id: Int)

    @Query("DELETE FROM mistakes WHERE ideaId = :ideaId")
    suspend fun deleteMistakesForIdea(ideaId: Int)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE ideaId = :ideaId ORDER BY timestamp ASC")
    fun getCommentsForIdea(ideaId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteCommentById(id: Int)

    @Query("DELETE FROM comments WHERE ideaId = :ideaId")
    suspend fun deleteCommentsForIdea(ideaId: Int)
}

// --- Database ---

@Database(
    entities = [
        IdeaEntity::class,
        WorkStepEntity::class,
        MistakeEntity::class,
        CommentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IdeaDatabase : RoomDatabase() {
    abstract fun ideaDao(): IdeaDao
    abstract fun workStepDao(): WorkStepDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun commentDao(): CommentDao
}
