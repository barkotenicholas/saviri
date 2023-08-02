package com.example.saviri.domain.usecases

import android.util.Patterns

class ValidatePassword {

    fun execute(password : String): ValidationResult {
        if(password.isBlank()){
            return ValidationResult(false,"Password Field Can't be Empty")
        }
        if( password.length < 8 ){
            return ValidationResult(false,"Password Field needs to consists of 8 characters")
        }

        return ValidationResult(
            success = true
        )
    }

    fun execute(password: String,repeatPassword: String):ValidationResult{
        if(repeatPassword.isBlank()){
            return ValidationResult(false,"Repeat Password Field Can't be Empty")

        }
        if(password != repeatPassword){
            return ValidationResult(false,"Password must be equal to repeat password")

        }

        return ValidationResult(
            success = true
        )
    }


}