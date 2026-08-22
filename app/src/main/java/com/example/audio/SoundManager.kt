package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-latency Sound Management System for Empire Tycoon.
 * Generates and caches pristine synthesizer audio waveforms for instant feedback
 * during gameplay (building clicks, revenue collection, daily rewards, upgrades).
 */
object SoundManager {

    private const val TAG = "SoundManager"
    private const val SAMPLE_RATE = 44100

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Cache of pre-synthesized PCM audio buffers
    private val soundBuffers = ConcurrentHashMap<SoundType, ShortArray>()

    var isSoundEnabled: Boolean = true

    enum class SoundType {
        TAP_CLICK,
        BUILDING_CLICK,
        COLLECT_REVENUE,
        DAILY_REWARD,
        UPGRADE_LEVEL,
        MANAGER_HIRE,
        ACHIEVEMENT_UNLOCK
    }

    init {
        // Pre-render all sound effects into memory to ensure 0ms playback latency
        try {
            soundBuffers[SoundType.TAP_CLICK] = generateTapSound()
            soundBuffers[SoundType.BUILDING_CLICK] = generateBuildingClickSound()
            soundBuffers[SoundType.COLLECT_REVENUE] = generateCollectRevenueSound()
            soundBuffers[SoundType.DAILY_REWARD] = generateDailyRewardSound()
            soundBuffers[SoundType.UPGRADE_LEVEL] = generateUpgradeSound()
            soundBuffers[SoundType.MANAGER_HIRE] = generateManagerSound()
            soundBuffers[SoundType.ACHIEVEMENT_UNLOCK] = generateAchievementSound()
            Log.d(TAG, "SoundManager initialized with ${soundBuffers.size} dynamic effects.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pre-synthesize audio buffers", e)
        }
    }

    /**
     * Play a sound effect if sounds are enabled.
     */
    fun playSound(type: SoundType, volumeMultiplier: Float = 1.0f) {
        if (!isSoundEnabled) return

        val pcmData = soundBuffers[type] ?: return
        audioScope.launch {
            try {
                playPcmTrack(pcmData, volumeMultiplier)
            } catch (e: Exception) {
                Log.w(TAG, "Error playing sound effect $type: ${e.message}")
            }
        }
    }

    // Convenience Methods
    fun playTap() = playSound(SoundType.TAP_CLICK, 0.6f)
    fun playBuildingClick() = playSound(SoundType.BUILDING_CLICK, 0.85f)
    fun playCollectRevenue() = playSound(SoundType.COLLECT_REVENUE, 0.95f)
    fun playDailyReward() = playSound(SoundType.DAILY_REWARD, 1.0f)
    fun playUpgrade() = playSound(SoundType.UPGRADE_LEVEL, 0.9f)
    fun playManagerHired() = playSound(SoundType.MANAGER_HIRE, 0.9f)
    fun playAchievement() = playSound(SoundType.ACHIEVEMENT_UNLOCK, 1.0f)

    /**
     * Streams the synthesized 16-bit PCM buffer to a lightweight one-shot AudioTrack.
     */
    private fun playPcmTrack(pcmData: ShortArray, volumeMultiplier: Float) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        val bufferSizeBytes = pcmData.size * 2
        val track = AudioTrack(
            audioAttributes,
            audioFormat,
            bufferSizeBytes,
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )

        val clampedVol = volumeMultiplier.coerceIn(0.0f, 1.0f)
        track.setVolume(clampedVol)
        track.write(pcmData, 0, pcmData.size)
        track.play()

        // Clean up resources once playback completes
        val durationMs = (pcmData.size.toDouble() / SAMPLE_RATE * 1000).toLong() + 50L
        audioScope.launch {
            try {
                kotlinx.coroutines.delay(durationMs)
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }

    // =========================================================================
    // Waveform Synthesizers (Rich Harmonics, Organic Envelopes, Crisp Transients)
    // =========================================================================

    /**
     * Crisp, tactile pop/click for UI taps and contract signing (35ms).
     */
    private fun generateTapSound(): ShortArray {
        val durationSec = 0.045
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            // Rapid pitch drop from 1200Hz to 350Hz with exponential decay
            val freq = 1200.0 * exp(-progress * 8.0) + 350.0
            val env = exp(-progress * 12.0)
            val wave = sin(2.0 * PI * freq * t) + 0.3 * sin(4.0 * PI * freq * t)
            val sample = (wave * env * 0.7 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Resonant building interaction chime with dual harmonics (120ms).
     */
    private fun generateBuildingClickSound(): ShortArray {
        val durationSec = 0.12
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            // Rising dual tone 520Hz + 880Hz with metallic attack
            val freq1 = 523.25 + (progress * 120.0) // C5
            val freq2 = 783.99 // G5
            val env = exp(-progress * 6.5) * (1.0 - exp(-progress * 50.0))
            val wave = 0.6 * sin(2.0 * PI * freq1 * t) + 0.4 * sin(2.0 * PI * freq2 * t)
            val sample = (wave * env * 0.75 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Cash register & shimmering coin collection chime (220ms).
     * Two sparkling bright bells in rapid succession (B5 -> E6 -> G#6).
     */
    private fun generateCollectRevenueSound(): ShortArray {
        val durationSec = 0.26
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val note1Freq = 987.77  // B5
        val note2Freq = 1318.51 // E6
        val note3Freq = 1661.22 // G#6

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples

            // Note 1 starts at t=0, Note 2 starts at t=0.06s, Note 3 at t=0.12s
            var wave = 0.0
            // Bell 1
            if (t >= 0.0) {
                val t1 = t
                val env1 = exp(-t1 * 18.0) * (1.0 - exp(-t1 * 200.0))
                wave += (sin(2.0 * PI * note1Freq * t1) + 0.35 * sin(4.0 * PI * note1Freq * t1)) * env1
            }
            // Bell 2
            if (t >= 0.06) {
                val t2 = t - 0.06
                val env2 = exp(-t2 * 14.0) * (1.0 - exp(-t2 * 200.0))
                wave += (sin(2.0 * PI * note2Freq * t2) + 0.4 * sin(4.0 * PI * note2Freq * t2)) * env2 * 1.2
            }
            // Bell 3 (High sparkle)
            if (t >= 0.12) {
                val t3 = t - 0.12
                val env3 = exp(-t3 * 10.0) * (1.0 - exp(-t3 * 200.0))
                wave += (sin(2.0 * PI * note3Freq * t3) + 0.25 * sin(6.0 * PI * note3Freq * t3)) * env3 * 1.3
            }

            val sample = (wave * 0.45 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Triumphant golden celebration fanfare for Daily Login Rewards and Chests (520ms).
     * Pentatonic ascending arpeggio with shimmering decay (C5 -> E5 -> G5 -> C6 -> E6).
     */
    private fun generateDailyRewardSound(): ShortArray {
        val durationSec = 0.58
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51) // C5, E5, G5, C6, E6
        val noteStarts = doubleArrayOf(0.0, 0.07, 0.14, 0.21, 0.28)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var wave = 0.0

            for (n in notes.indices) {
                val start = noteStarts[n]
                if (t >= start) {
                    val tn = t - start
                    val freq = notes[n]
                    // Rich bell chime envelope
                    val env = exp(-tn * 8.0) * (1.0 - exp(-tn * 150.0))
                    val tone = sin(2.0 * PI * freq * tn) +
                            0.35 * sin(4.0 * PI * freq * tn) +
                            0.15 * sin(6.0 * PI * freq * tn)
                    wave += tone * env
                }
            }

            val sample = (wave * 0.35 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Uplifting ascending power-up chord for Level Up & Upgrades (180ms).
     */
    private fun generateUpgradeSound(): ShortArray {
        val durationSec = 0.22
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 440.0 // A4
        val f2 = 554.37 // C#5
        val f3 = 659.25 // E5
        val f4 = 880.0 // A5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var wave = 0.0

            if (t >= 0.0) {
                val t1 = t
                val env = exp(-t1 * 14.0) * (1.0 - exp(-t1 * 200.0))
                wave += sin(2.0 * PI * f1 * t1) * env
            }
            if (t >= 0.05) {
                val t2 = t - 0.05
                val env = exp(-t2 * 12.0) * (1.0 - exp(-t2 * 200.0))
                wave += sin(2.0 * PI * f2 * t2) * env
            }
            if (t >= 0.10) {
                val t3 = t - 0.10
                val env = exp(-t3 * 9.0) * (1.0 - exp(-t3 * 200.0))
                wave += (sin(2.0 * PI * f3 * t3) + 0.4 * sin(2.0 * PI * f4 * t3)) * env * 1.3
            }

            val sample = (wave * 0.42 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Professional executive hiring chime (180ms).
     */
    private fun generateManagerSound(): ShortArray {
        val durationSec = 0.25
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val f1 = 587.33 // D5
        val f2 = 880.00 // A5

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / numSamples
            val env = exp(-progress * 8.0) * (1.0 - exp(-progress * 40.0))
            val wave = 0.55 * sin(2.0 * PI * f1 * t) + 0.45 * sin(2.0 * PI * f2 * t)
            val sample = (wave * env * 0.7 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }

    /**
     * Achievement unlock victory fanfare (350ms).
     */
    private fun generateAchievementSound(): ShortArray {
        val durationSec = 0.38
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val notes = doubleArrayOf(587.33, 739.99, 880.0, 1174.66) // D5, F#5, A5, D6
        val starts = doubleArrayOf(0.0, 0.07, 0.14, 0.20)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var wave = 0.0
            for (n in notes.indices) {
                val st = starts[n]
                if (t >= st) {
                    val tn = t - st
                    val env = exp(-tn * 9.0) * (1.0 - exp(-tn * 150.0))
                    wave += (sin(2.0 * PI * notes[n] * tn) + 0.3 * sin(4.0 * PI * notes[n] * tn)) * env
                }
            }
            val sample = (wave * 0.4 * Short.MAX_VALUE).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return buffer
    }
}
