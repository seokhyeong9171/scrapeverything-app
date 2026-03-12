package com.scrapeverything.app.util

import android.util.Patterns

object Validators {

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return validatePassword(password).isValid
    }

    data class PasswordValidation(
        val isLengthValid: Boolean,
        val hasLetter: Boolean,
        val hasDigit: Boolean
    ) {
        val isValid: Boolean get() = isLengthValid && hasLetter && hasDigit
    }

    fun validatePassword(password: String): PasswordValidation {
        return PasswordValidation(
            isLengthValid = password.length in 7..15,
            hasLetter = password.any { it.isLetter() },
            hasDigit = password.any { it.isDigit() }
        )
    }

    fun isValidNickname(nickname: String): Boolean {
        return nickname.isNotBlank()
    }
}
