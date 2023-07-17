package com.example.saviri.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saviri.data.Resource
import com.example.saviri.repository.auth.AuthRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository:AuthRepoImpl
) : ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: MutableStateFlow<Resource<FirebaseUser>?> = _loginFlow

    fun login (email:String,password:String) = viewModelScope.launch {

        _loginFlow.value = Resource.Loading
        val result = repository.login(email,password)
        _loginFlow.value = result

    }



}