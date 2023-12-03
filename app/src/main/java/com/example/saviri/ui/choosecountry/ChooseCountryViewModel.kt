package com.example.saviri.ui.choosecountry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.domain.usecases.ValidateEmail
import com.example.saviri.domain.usecases.ValidateName
import com.example.saviri.network.ApiClient
import com.example.saviri.network.models.Countries
import com.example.saviri.network.models.Symbols
import com.example.saviri.repository.api.ApiRepoeImpl
import com.example.saviri.ui.home.HomeViewModel
import com.example.saviri.ui.login.LoginState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.log

class ChooseCountryViewModel(
    private val repository:ApiRepoeImpl,
    private val validateName: ValidateName = ValidateName(),

    ): ViewModel() {

    private var _countries = MutableStateFlow<Countries?>(Countries(success = false, Symbols()))
    private val countries = _countries.asStateFlow()

    private var validationEventChannel = Channel<ChooseCountryState>()
    val validationchannel = validationEventChannel.receiveAsFlow()


    suspend fun getCountries(){

        validationEventChannel.send(ChooseCountryState.Loading)

        viewModelScope.launch {

            var results = repository.getSupportedCountries()
            Log.d("*-*-***-*-*-**-*-*", "getCountries: After results")
            if(results.success == true){
                Log.i("TAG---------", "getCountries: ${results.symbols}")
                _countries.value = _countries.value?.copy(results.success,results.symbols)
                validationEventChannel.send(ChooseCountryState.OnDataRecieve(
                    Countries(results.success,results.symbols)
                ))
                Log.i("**********0", "getCountries: ${_countries.value?.success}")
            }else{
                Log.i("---------------------", "getCountries: ${results.success}")
            }
        }
    }
    companion object {
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
//                val retrofitService = ApiClient.getClient()
                var repo = ApiRepoeImpl(ApiClient)
                return ChooseCountryViewModel(repo) as T
            }
        }
    }


}

sealed class  ChooseCountryState{

    object Loading:ChooseCountryState()
    data class OnDataRecieve(val country :Countries) : ChooseCountryState()

    data class ShoppingNameError(val message: String): ChooseCountryState()


}