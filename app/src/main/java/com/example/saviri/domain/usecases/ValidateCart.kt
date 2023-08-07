package com.example.saviri.domain.usecases

class ValidateCart {

    fun execute_name(input:String): ValidationResult {

        if(input.isBlank()){
            return ValidationResult(false,"Input can not be blank")
        }

        return ValidationResult(true)
    }

    fun execute_price(input: String): ValidationResult{
        if(input.isBlank()){
            return ValidationResult(false,"Input can not be blank")
        }

        try {
           val num = input.toInt()

        }catch (e:Exception){
            return ValidationResult(false,"Provide valida integer")
        }

        return ValidationResult(true)
    }
}