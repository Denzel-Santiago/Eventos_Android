package com.proyecto.eventos.core.hardware.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class VibrationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : VibrationManager {

    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager)
                .defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun vibrateSuccess() {
        // Patrón corto: 100ms - pausa - 100ms
        val timings = longArrayOf(0, 100, 100)
        val amplitudes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intArrayOf(0, 255, 0)
        } else {
            null
        }

        val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createWaveform(timings, -1)
        }

        vibrator.vibrate(effect)
    }

    override fun vibrateError() {
        // Patrón largo: 500ms continuo
        val timings = longArrayOf(0, 500)
        val amplitudes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intArrayOf(0, 255)
        } else {
            null
        }

        val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createWaveform(timings, -1)
        }

        vibrator.vibrate(effect)
    }

    override fun vibrateLight() {
        // Toque suave: 50ms
        val timings = longArrayOf(0, 50)
        val amplitudes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intArrayOf(0, 128) // Amplitud media para vibración suave
        } else {
            null
        }

        val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createWaveform(timings, -1)
        }

        vibrator.vibrate(effect)
    }
}
