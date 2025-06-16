package com.cursointermedio.myapplication.utils.extensions

import java.math.BigDecimal
import java.math.RoundingMode

object E1RMFormulas {

    /**
     * Brzycki: E1RM = peso / (1.0278 - 0.0278 × reps)
     */
    fun brzycki(peso: Int, reps: Int): Double {
        return if (reps == 0 || peso == 0){
            0.00
        }else{
            peso / (1.0278 - 0.0278 * reps)
        }
    }
}