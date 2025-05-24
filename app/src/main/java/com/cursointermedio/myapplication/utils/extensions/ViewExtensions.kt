package com.cursointermedio.myapplication.utils.extensions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.cursointermedio.myapplication.R
import com.google.android.material.snackbar.Snackbar
import io.grpc.Context

@SuppressLint("ClickableViewAccessibility")
fun View.setupTouchAction(onClickAction: () -> Unit) {
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> v.alpha = 0.4F
            MotionEvent.ACTION_UP -> {
                v.alpha = 1F
                val x = event.x
                val y = event.y
                if (x >= 0 && x <= v.width && y >= 0 && y <= v.height) {
                    onClickAction()
                }
            }

            MotionEvent.ACTION_CANCEL -> v.alpha = 1F
        }
        true
    }
}

@SuppressLint("ClickableViewAccessibility")
fun View.setupTouchActionRecyclerView(onClickAction: () -> Unit) {
    var alphaAnimator: ObjectAnimator? = null
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                alphaAnimator?.cancel()
                alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0.4f) // Cambia de 1 a 0.4
                alphaAnimator?.duration = 500
                alphaAnimator?.start()
            }

            MotionEvent.ACTION_UP -> {
                alphaAnimator?.cancel()
                v.alpha = 1F
                val x = event.x
                val y = event.y
                if (x >= 0 && x <= v.width && y >= 0 && y <= v.height) {
                    onClickAction()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                alphaAnimator?.cancel()
                v.alpha = 1F
            }
        }
        true
    }
}

fun showSnackbar(view: View, msg: String, context: android.content.Context) {
    Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(context, R.color.redDark))
        .setTextColor(ContextCompat.getColor(context, R.color.white))
        .show()
}

fun isItemBelowThreshold(view: View): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)

    val itemTop = location[1] // Coordenada Y del top del item
    val itemBottom = itemTop + view.height // Coordenada Y del bottom del item

    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val percentageThreshold = 0.75f

    val threshold = screenHeight * percentageThreshold

    // Verificar si el borde inferior del item ha superado el umbral (80%)
    return if (itemBottom > threshold) {
        true
    } else {
        false
    }
}