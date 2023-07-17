package com.example.saviri.repository.auth

import com.example.saviri.data.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepo {

    val currentUser:FirebaseUser?
    suspend fun login(email:String, password:String):Resource<FirebaseUser>
    suspend fun signup(name:String, email : String, password: String):Resource<FirebaseUser>
    fun logout()
}