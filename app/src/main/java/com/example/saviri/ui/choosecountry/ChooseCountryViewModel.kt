package com.example.saviri.ui.choosecountry

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.network.ApiClient
import com.example.saviri.network.models.Countries
import com.example.saviri.network.models.Symbols
import com.example.saviri.repository.api.ApiRepoeImpl
import com.example.saviri.ui.home.HomeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.log

class ChooseCountryViewModel(
    private val repository:ApiRepoeImpl
): ViewModel() {

    private var _countries = MutableStateFlow<Countries?>(Countries(success = false, Symbols()))
    private val countries = _countries.asStateFlow()

    private var validationEventChannel = Channel<ChooseCountryState>()
    val validationchannel = validationEventChannel.receiveAsFlow()

    fun getCountries(){
        viewModelScope.launch {
            Log.i("sads", "getCountries: list all supported countess")
            var results = repository.getSupportedCountries()
            if(results.success == true){
                Log.i("TAG---------", "getCountries: ${results.symbols}")
                _countries.value = _countries.value?.copy(results.success,results.symbols)
                validationEventChannel.send(ChooseCountryState.OnDataRecieve(
                    Countries(results.success,results.symbols)
                ))
                Log.i("**********0", "getCountries: ${_countries.value?.success}")

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

sealed class ChooseCountryState{
    data class OnDataRecieve(val country :Countries) : ChooseCountryState()
}