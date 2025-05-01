package com.cursointermedio.myapplication.utils.extensions

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

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