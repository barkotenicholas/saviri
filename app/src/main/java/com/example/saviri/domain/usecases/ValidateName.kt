package com.example.saviri.domain.usecases

class ValidateName {

    fun execute(name:String):ValidationResult{

        if(name.isBlank()){
            return ValidationResult(
                success = false,
                errorMessage = "Name is blank"
            )
        }

        return ValidationResult(
            success = true
        )
    }

}