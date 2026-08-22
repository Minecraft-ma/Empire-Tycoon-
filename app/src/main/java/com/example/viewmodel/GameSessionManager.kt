package com.example.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

sealed interface SessionState {
    object Idle : SessionState
    data class Active(
        val sessionId: String,
        val gameType: String,
        val startTime: Long,
        val clicksCount: Int = 0,
        val lastClickTime: Long = 0L,
        val totalValidInputs: Int = 0
    ) : SessionState
}

class GameSessionManager {
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun startSession(gameType: String): String {
        val newId = UUID.randomUUID().toString()
        _sessionState.value = SessionState.Active(
            sessionId = newId,
            gameType = gameType,
            startTime = System.currentTimeMillis()
        )
        return newId
    }

    /**
     * Registers a tap or input event in the active session.
     * Enforces rate-limiting: returns true if input is valid (not spamming/too fast), false otherwise.
     */
    fun registerInput(sessionId: String): Boolean {
        val current = _sessionState.value
        if (current !is SessionState.Active || current.sessionId != sessionId) {
            return false // Invalid or inactive session
        }

        val now = System.currentTimeMillis()
        
        // Enforce anti-spam / rate-limiting:
        // Max 12 clicks per second, meaning average delay between taps must be at least 80ms.
        // If two taps occur within 60ms, it is flagged as auto-clicker/spam.
        val timeSinceLastClick = now - current.lastClickTime
        val isSpam = timeSinceLastClick < 60L && current.clicksCount > 0

        _sessionState.update { state ->
            if (state is SessionState.Active && state.sessionId == sessionId) {
                state.copy(
                    clicksCount = state.clicksCount + 1,
                    lastClickTime = now,
                    totalValidInputs = if (isSpam) state.totalValidInputs else state.totalValidInputs + 1
                )
            } else state
        }

        return !isSpam
    }

    /**
     * Validates and completes the minigame session.
     * Returns true if the performance matches physical constraints and sessionId is valid.
     */
    fun validateAndComplete(sessionId: String, declaredScore: Int, expectedMaxScore: Int): Boolean {
        val current = _sessionState.value
        if (current !is SessionState.Active || current.sessionId != sessionId) {
            return false // Unauthorized session submission
        }

        val now = System.currentTimeMillis()
        val durationMs = now - current.startTime

        // Score anomaly checks:
        // 1. Score cannot exceed expectedMaxScore.
        // 2. Score cannot exceed total valid inputs registered.
        // 3. Inputs must align with elapsed duration (e.g. max 12 clicks/sec).
        val maxFeasibleTaps = (durationMs / 1000.0) * 12.0 + 3.0 // Include a tiny grace buffer of 3 taps
        
        val scoreIsValid = declaredScore <= expectedMaxScore &&
                           declaredScore <= current.totalValidInputs &&
                           current.totalValidInputs <= maxFeasibleTaps

        // End the session
        _sessionState.value = SessionState.Idle
        return scoreIsValid
    }

    fun endSession() {
        _sessionState.value = SessionState.Idle
    }
}
