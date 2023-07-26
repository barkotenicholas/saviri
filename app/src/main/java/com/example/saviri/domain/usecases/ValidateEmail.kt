package com.example.saviri.domain.usecases


import android.util.Log
import android.util.Patterns

class ValidateEmail {

    fun execute(email: String): ValidationResult {
        Log.d("TAG", "execute: validating email $email")
        if(email.isBlank()){
            return ValidationResult(false,"The Email Can't be blank")
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Log.d("TAG", "execute: validating email $email")

            return ValidationResult(false,"Not a valid Email")
        }
        return ValidationResult(
            success = true
        )
    }

}