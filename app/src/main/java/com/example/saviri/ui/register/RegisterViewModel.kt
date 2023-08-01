package com.example.saviri.ui.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.Resource
import com.example.saviri.repository.auth.AuthRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: AuthRepoImpl
) : ViewModel() {

    private val TAG: String ="Fragment"
    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: MutableStateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val _state = MutableStateFlow(RegisterFormState())
    val state = _state.asStateFlow()


    private val _name = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _repeatPassword = MutableStateFlow("")
    private val _email = MutableStateFlow("")

    init {
        if(repository.currentUser != null){
            _signUpFlow.value = Resource.Success(repository.currentUser!!)
        }
    }

    fun signUp() = viewModelScope.launch{

        _signUpFlow.value = Resource.Loading
        Log.d(TAG, "signUp: ..................")
        val result =  repository.signup(_name.value,_email.value,_password.value)
        Log.d(TAG, "signsasdasdUp: .................. $result")

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
                _state.value = _state.value.copy(passwordError = event.repeatPassword)
            }
            RegisterFormEvent.Submit -> {
                validateInputs()
            }
        }
    }

    private fun validateInputs() {



    }

    fun setName(name: String){
        Log.d(TAG, "setName:  name change ${name}")
        _name.value = name
    }
    fun setPassword(password: String){
        _password.value = password
    }
    fun setRepeatPassword(repeatPassword: String){
        _repeatPassword.value = repeatPassword
    }
    fun setEmail(email: String){
        _email.value = email
    }


    val isSubmitEnabled: Flow<Boolean> = combine(_name, _password, _repeatPassword,_email) { name , password, repeatPassword , email ->
        val regexString = "[a-zA-Z]+"
        val regexEmail = "[a-zA-Z]+"
        val isNameCorrect = name.matches(regexString.toRegex())
        val isPasswordCorrect = password.length > 8
        val isRepeatPasswordCorrect = repeatPassword.length > 8
        val isEmailCorrect = true
        return@combine isNameCorrect and isPasswordCorrect and isRepeatPasswordCorrect and isEmailCorrect
    }


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

sealed class RegisterFormEvent{
    data class NameChanged(val name:String):RegisterFormEvent()
    data class EmailChanged(val email: String):RegisterFormEvent()
    data class  PasswordChanged(val password: String):RegisterFormEvent()
    data class RepeatPassword(val repeatPassword: String):RegisterFormEvent()
    object Submit:RegisterFormEvent()

}