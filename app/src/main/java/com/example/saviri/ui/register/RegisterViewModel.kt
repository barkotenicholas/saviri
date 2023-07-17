package com.example.saviri.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saviri.data.Resource
import com.example.saviri.repository.auth.AuthRepoImpl
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepoImpl
) : ViewModel() {

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: MutableStateFlow<Resource<FirebaseUser>?> = _signUpFlow


    fun signUp(name: String,email:String,password:String) = viewModelScope.launch{

        _signUpFlow.value = Resource.Loading
        val result =  repository.signup(name,email,password)
        _signUpFlow.value = result


    }

}