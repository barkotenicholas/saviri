package com.example.saviri.repository.auth

import android.util.Log
import com.example.saviri.data.Resource
import com.example.saviri.util.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthRepoImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepo {
    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {

        return try {

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)

        }catch (e:Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val firebaseAuth = Firebase.auth
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())?.await()
            Resource.Success(result.user!!)
        }catch (e:Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }    }

    override fun logout() {

        firebaseAuth.signOut()
    }
}