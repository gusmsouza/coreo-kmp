package com.coreo.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import com.coreo.model.Goal
import com.coreo.model.WorkoutSession
import kotlinx.datetime.Instant

// MARK: - Type Converters

class Converters {
    @TypeConverter
    fun fromIntList(value: String): List<Int> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").mapNotNull { it.trim().toIntOrNull() }
    }

    @TypeConverter
    fun toIntList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun fromInstant(value: Long): Instant = Instant.fromEpochMilliseconds(value)

    @TypeConverter
    fun toInstant(instant: Instant): Long = instant.toEpochMilliseconds()
}

// MARK: - Entities

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val date: Instant,
    val duration: Int,
    @ColumnInfo(name = "is_set") val isSet: Boolean,
    val durations: List<Int>,
    @ColumnInfo(name = "total_reps") val totalReps: Int,
    @ColumnInfo(name = "rest_duration") val restDuration: Int
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "target_workouts") val targetWorkouts: Int,
    val weeks: Int,
    @ColumnInfo(name = "start_date") val startDate: Instant,
    @ColumnInfo(name = "completed_workouts") val completedWorkouts: Int,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)

// MARK: - Mappers

fun WorkoutSession.toEntity() = WorkoutSessionEntity(
    id = id, date = date, duration = duration,
    isSet = isSet, durations = durations,
    totalReps = totalReps, restDuration = restDuration
)

fun WorkoutSessionEntity.toModel() = WorkoutSession(
    id = id, date = date, duration = duration,
    isSet = isSet, durations = durations,
    totalReps = totalReps, restDuration = restDuration
)

fun Goal.toEntity() = GoalEntity(
    id = id, targetWorkouts = targetWorkouts, weeks = weeks,
    startDate = startDate, completedWorkouts = completedWorkouts, isActive = isActive
)

fun GoalEntity.toModel() = Goal(
    id = id, targetWorkouts = targetWorkouts, weeks = weeks,
    startDate = startDate, completedWorkouts = completedWorkouts, isActive = isActive
)

// MARK: - DAOs

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    suspend fun getAll(): List<WorkoutSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSessionEntity)

    @Delete
    suspend fun delete(session: WorkoutSessionEntity)

    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAll()
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    suspend fun getAll(): List<GoalEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity)

    @Update
    suspend fun update(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    @Query("DELETE FROM goals")
    suspend fun deleteAll()
}

// MARK: - Database

@Database(
    entities  = [WorkoutSessionEntity::class, GoalEntity::class],
    version   = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun goalDao(): GoalDao
}

// MARK: - Builder

fun buildDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "coreo.db"
    ).build()
