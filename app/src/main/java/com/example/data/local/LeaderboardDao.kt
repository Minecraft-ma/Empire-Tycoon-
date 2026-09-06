package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaderboardDao {

    @Query("SELECT * FROM leaderboard_scores ORDER BY totalCashEarned DESC LIMIT :limit")
    fun getTopScores(limit: Int = 100): Flow<List<LeaderboardScoreEntity>>

    @Query("SELECT * FROM leaderboard_scores WHERE isPlayerRun = 1 ORDER BY totalCashEarned DESC")
    fun getPlayerRuns(): Flow<List<LeaderboardScoreEntity>>

    @Query("SELECT * FROM leaderboard_scores ORDER BY totalCashEarned DESC")
    fun getAllScores(): Flow<List<LeaderboardScoreEntity>>

    @Query("SELECT COUNT(*) FROM leaderboard_scores WHERE totalCashEarned > :score")
    suspend fun getRankForScore(score: Double): Int

    @Query("SELECT * FROM leaderboard_scores WHERE isPlayerRun = 1 ORDER BY totalCashEarned DESC LIMIT 1")
    suspend fun getBestPlayerScore(): LeaderboardScoreEntity?

    @Query("SELECT COUNT(*) FROM leaderboard_scores")
    suspend fun getScoresCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: LeaderboardScoreEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScores(scores: List<LeaderboardScoreEntity>)

    @Query("DELETE FROM leaderboard_scores WHERE id = :id")
    suspend fun deleteScoreById(id: Long)

    @Query("DELETE FROM leaderboard_scores WHERE isPlayerRun = 1")
    suspend fun clearPlayerScores()

    @Query("DELETE FROM leaderboard_scores WHERE isPlayerRun = 0")
    suspend fun deleteNonPlayerScores()

    @Query("DELETE FROM leaderboard_scores")
    suspend fun clearAll()
}
