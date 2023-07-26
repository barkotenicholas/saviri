package com.example.saviri.domain.usecases


import android.util.Patterns

class ValidateEmail {

    fun execute(email: String): ValidationResult {
        if(email.isBlank()){
            return ValidationResult(false,"The Email Can't be blank")
        }

        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return ValidationResult(false,"Not a valid Email")
        }
        return ValidationResult(
            success = true
        )
    }

}