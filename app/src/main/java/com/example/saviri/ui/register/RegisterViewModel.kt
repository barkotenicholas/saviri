package com.example.saviri.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.Resource
import com.example.saviri.domain.usecases.ValidateEmail
import com.example.saviri.domain.usecases.ValidateName
import com.example.saviri.domain.usecases.ValidatePassword
import com.example.saviri.repository.auth.AuthRepoImpl
import com.example.saviri.ui.login.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepoImpl,
    private val validatePassword: ValidatePassword = ValidatePassword(),
    private val validateEmail: ValidateEmail = ValidateEmail(),
    private val validateName: ValidateName = ValidateName()
) : ViewModel() {

    private val TAG: String ="Fragment"
    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: MutableStateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val _state = MutableStateFlow(RegisterFormState())
    val state = _state.asStateFlow()

    private val validationEventChannel = Channel<RegisterState>()
    val validationEvents = validationEventChannel.receiveAsFlow()


    init {
        if(repository.currentUser != null){
            _signUpFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun signUp() = viewModelScope.launch{

        _signUpFlow.value = Resource.Loading

        val result =  repository.signup(name =_state.value.name, email = _state.value.email, password = _state.value.password)

        _signUpFlow.value = result

    }

    fun event(event: RegisterFormEvent){
        when(event){
            is RegisterFormEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is RegisterFormEvent.NameChanged -> {
                _state.value = _state.value.copy(name = event.name)
            }
            is RegisterFormEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is RegisterFormEvent.RepeatPassword -> {
                _state.value = _state.value.copy(repeatPassword = event.repeatPassword)
            }
            RegisterFormEvent.Submit -> {
                validateInputs()
            }
        }
    }

    private fun validateInputs() {

        viewModelScope.launch {
            validationEventChannel.send(RegisterState.Clear)
        }

        val emailResult = validateEmail.execute(_state.value.email)
        val nameResult = validateName.execute(_state.value.name)
        val passwordResult = validatePassword.execute(_state.value.password)
        val repeatPasswordResult = validatePassword.execute(_state.value.password,_state.value.repeatPassword)

        val hasError = listOf(
            emailResult,
            nameResult,
            passwordResult,
            repeatPasswordResult
        ).any {
            !it.success
        }

        if(hasError){
            _state.value = _state.value.copy(repeatPasswordError = repeatPasswordResult.errorMessage,emailError = emailResult.errorMessage, passwordError = passwordResult.errorMessage, nameError = nameResult.errorMessage)

            viewModelScope.launch {
                emailResult.errorMessage?.let {
                    RegisterState.EmailError(it)
                }?.let {
                    validationEventChannel.send(it)
                }
                nameResult.errorMessage?.let {
                    RegisterState.NameError(it)
                }?.let {
                    validationEventChannel.send(it)
                }
                passwordResult.errorMessage?.let {
                    RegisterState.PasswordError(it)
                }?.let {
                    validationEventChannel.send(it)
                }
                repeatPasswordResult.errorMessage?.let {
                    RegisterState.RepeatPasswordError(it)
                }?.let {
                    validationEventChannel.send(it)
                }
            }
            return
        }

        register()

    }

    private fun register() = signUp()


    companion object {
        val Factory : ViewModelProvider.Factory = object :ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {


                val repository = AuthRepoImpl(FirebaseAuth.getInstance())
                return RegisterViewModel(
                    repository
                )as T
            }
        }
    }
}

data class RegisterFormState(
    val name: String = "",
    val nameError:String? = null,
    val email: String = "",
    val emailError:String? = null,
    val password: String= "",
    val passwordError:String?=null,
    val repeatPassword: String= "",
    val repeatPasswordError:String?=null,
)

sealed class RegisterState{
    data class EmailError(val message: String): RegisterState()
    data class PasswordError(val message: String): RegisterState()
    data class RepeatPasswordError(val message: String):RegisterState()
    data class NameError(val message: String):RegisterState()
    object Success:RegisterState()
    object Clear:RegisterState()
}

sealed class RegisterFormEvent{
    data class NameChanged(val name:String):RegisterFormEvent()
    data class EmailChanged(val email: String):RegisterFormEvent()
    data class  PasswordChanged(val password: String):RegisterFormEvent()
    data class RepeatPassword(val repeatPassword: String):RegisterFormEvent()
    object Submit:RegisterFormEvent()

}