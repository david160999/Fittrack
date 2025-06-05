package com.cursointermedio.myapplication.ui.settings

enum class Language(val code: String, private val label: String) {
    ENGLISH("en", "English"),
    SPANISH("es", "Español");

    companion object {
        fun fromCode(code: String): Language =
            values().find { it.code == code } ?: SPANISH
    }

    override fun toString(): String = label

}
