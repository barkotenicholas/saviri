package com.example.saviri.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.Resource
import com.example.saviri.domain.usecases.ValidateEmail
import com.example.saviri.domain.usecases.ValidatePassword
import com.example.saviri.repository.auth.AuthRepoImpl
import com.example.saviri.ui.register.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository:AuthRepoImpl,
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validatePassword: ValidatePassword = ValidatePassword()
) : ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow = _loginFlow.asStateFlow()

    private val _state = MutableStateFlow(LoginFormState())
    var state = _state.asStateFlow().value

    private val validationEventChannel = Channel<LoginState>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    init {
        if(repository.currentUser != null){
            _loginFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun login () = viewModelScope.launch {

        _loginFlow.value = Resource.Loading
        val result = repository.login(state.email,state.password)
        _loginFlow.value = result
    }

    fun event(event: LoginFormEvent){
        when(event){
            is LoginFormEvent.EmailChanged -> {

                Log.d("TAG", "text changed: -------------------")

                _state.value = _state.value.copy(email = event.email)
            }
            is LoginFormEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.pass)
            }
            LoginFormEvent.Submit -> {
                passValidation()
            }
        }
    }

    private fun passValidation() {

        Log.d("TAG", "passValidation: ${state.email}")
        Log.d("TAG", "passValidation: ${state.password}")

        val emailResult = validateEmail.execute(state.email)
        val passwordResult = validatePassword.execute(state.password)

        Log.d("TAG", "passValidation: -------------------")
        val hasError = listOf(
            emailResult,
            passwordResult
        ).any { !it.success }
        Log.d("TAG", "passValidation: ------------------- ${emailResult.errorMessage}")
        Log.d("TAG", "passValidation: ------------------- ${state.emailError}")

        if (hasError){
            Log.d("TAG", "*****************: ----------------------------- ${_state.value}")
            _state.value = _state.value.copy( emailError = emailResult.errorMessage, passwordError =  passwordResult.errorMessage)
            Log.d("TAG", "passValidation: ------------------- ${state.emailError}")

            viewModelScope.launch {
                emailResult.errorMessage?.let {
                    LoginState.Error(it)
                }?.let {
                    validationEventChannel.send(it)
                }
                passwordResult.errorMessage?.let {
                    LoginState.Error(it)
                }?.let {
                    validationEventChannel.send(it)
            }
            }
            return
        }
        login()
    }

    companion object {
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val savedStateHandle = extras.createSavedStateHandle()
                val repository = AuthRepoImpl(FirebaseAuth.getInstance())
                return LoginViewModel(
                    repository
                ) as T
            }
        }
    }

}

data class LoginFormState (
        val email : String = "",
        val emailError : String? = null,
        val password : String = "",
        val passwordError : String? = null
        )

sealed class LoginFormEvent {
    data class EmailChanged(val email:String):LoginFormEvent()
    data class PasswordChanged(val pass:String):LoginFormEvent()
    object Submit:LoginFormEvent()
}



sealed class LoginState{
    object Success : LoginState()
    data class Error(val message : String) : LoginState()
    object Loading : LoginState()
    object Empty : LoginState()
}