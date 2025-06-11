package com.cursointermedio.myapplication.utils.extensions

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursointermedio.myapplication.R
import com.cursointermedio.myapplication.ui.home.adapter.HomeMenuOptionAdapter
import com.google.android.material.snackbar.Snackbar

// Extensiones y utilidades para interacción táctil, animaciones y menús contextuales en vistas.

@SuppressLint("ClickableViewAccessibility")
fun View.setupTouchAction(onClickAction: () -> Unit) {
    // Cambia la opacidad de la vista al presionar y ejecuta la acción al soltar dentro de los límites.
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
    // Igual que setupTouchAction pero con animación suave de opacidad para listas.
    var alphaAnimator: ObjectAnimator? = null
    this.setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                alphaAnimator?.cancel()
                alphaAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, 0.4f) // De 1 a 0.4
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

// Muestra un Snackbar personalizado con color de fondo y texto.
fun showSnackbar(view: View, msg: String, context: android.content.Context) {
    Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(context, R.color.redDark))
        .setTextColor(ContextCompat.getColor(context, R.color.white))
        .show()
}

// Determina si una vista está por debajo del 75% de la pantalla (útil para mostrar menús contextuales).
fun isItemBelowThreshold(view: View): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)

    val itemTop = location[1]
    val itemBottom = itemTop + view.height

    val screenHeight = Resources.getSystem().displayMetrics.heightPixels
    val percentageThreshold = 0.75f

    val threshold = screenHeight * percentageThreshold

    // Verifica si la parte inferior del item supera el umbral (75%)
    return itemBottom > threshold
}

// Crea y muestra un menú emergente contextual en una vista.
// El menú contiene una opción (por ejemplo, eliminar) y ejecuta la acción correspondiente al seleccionarla.
fun createMenuOption(
    context: android.content.Context,
    view: View,
    text: String,
    onDelete: () -> Unit
) {
    val popupView = LayoutInflater.from(context).inflate(R.layout.popup_menu_training, null)

    val popupWindow = PopupWindow(
        popupView,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT,
        true
    )

    val recyclerView = popupView.findViewById<RecyclerView>((R.id.rvTrainingMenu))
    recyclerView.layoutManager = LinearLayoutManager(context)

    val items = listOf(text)

    val adapter = HomeMenuOptionAdapter(items) { position ->
        when (position) {
            0 -> onDelete()
        }
        popupWindow.dismiss()
    }

    recyclerView.adapter = adapter

    popupWindow.apply {
        isFocusable = true
        isOutsideTouchable = true
        elevation = 20f
        isClippingEnabled = true
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        animationStyle = R.style.MenuTRainingPopupFadeAnimation
    }
    popupWindow.showAsDropDown(view, 0, 0)
}