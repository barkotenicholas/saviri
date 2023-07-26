package com.example.saviri.domain.usecases

data class ValidationResult(
    val success:Boolean,
    val errorMessage:String? = null
)