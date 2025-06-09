package com.cursointermedio.myapplication.utils.extensions

import java.util.Locale

object E1RMFormulas {

    /**
     * Brzycki: E1RM = peso / (1.0278 - 0.0278 × reps)
     */
    fun brzycki(peso: Int, reps: Int): Double {
        val resultado = peso / (1.0278 - 0.0278 * reps)
        return String.format(Locale.US, "%.2f", resultado).toDouble()
    }
}