package com.example.saviri.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.ShoppingItem
import com.example.saviri.domain.usecases.ValidateCart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel

class HomeViewModel(
    private val  validateCart: ValidateCart = ValidateCart()
) : ViewModel() {

    private val _state = MutableStateFlow(CartFormState())
    val state = _state.asStateFlow()

    private val cartValidationEventChannel = Channel<CartState>()
    val cartValidationChannel = cartValidationEventChannel.receiveAsFlow()

    private val _currencyState = MutableStateFlow(CurrencyFormState())
    val currencyState = _currencyState.asStateFlow()

    fun event(event: CartFormEvent){
        when(event){
            is CartFormEvent.ItemNameChanged -> {
                _state.value = _state.value.copy(itemName = event.itemName)
            }
            is CartFormEvent.ItemPriceChanged -> {
                _state.value = _state.value.copy(itemPrice = event.ItemPrice)
            }
            CartFormEvent.Submit -> {
                validateInputs()
            }
        }
    }

    fun currencyEvent(event: CurrencyFormEvent){
        when (event){
            is CurrencyFormEvent.CurrencyChanged -> {
                _currencyState.value = _currencyState.value.copy(currencyRate = event.currency)
            }
        }
    }

    private fun validateInputs() {
        val itemNameResult = validateCart.execute_name(_state.value.itemName)
        val itemPriceResult = validateCart.execute_price(_state.value.itemPrice)

        viewModelScope.launch {
            cartValidationEventChannel.send(CartState.Clear)
        }

        val hasError = listOf(
            itemNameResult,
            itemPriceResult
        ).any{
            !it.success
        }

        if(hasError){

            _state.value = _state.value.copy(itemNameError = itemNameResult.errorMessage, itemPriceError = itemPriceResult.errorMessage)

            viewModelScope.launch { 
                itemNameResult.errorMessage?.let {
                    CartState.ItemNameError(it)
                }?.let {
                    cartValidationEventChannel.send(it)
                }
                itemPriceResult.errorMessage?.let {
                    CartState.ItemPriceError(it)
                }?.let {
                    cartValidationEventChannel.send(it)
                }
            }

            return
        }

        viewModelScope.launch {
            cartValidationEventChannel.send(CartState.Success(
                ShoppingItem(_state.value.itemName,
                    _state.value.itemPrice.toDouble(),
                    1
                )
            ))
        }

    }


    companion object {
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return HomeViewModel()as T
            }
        }
    }

}

data class CartFormState(
    val itemName : String = "",
    val itemNameError : String?=null,
    val itemPrice : String="",
    val itemPriceError:String?=null
)

data class CurrencyFormState(
    val currencyRate : String = "",
    val currencyRateError : String ?= null
)

sealed class CurrencyFormEvent{
    data class CurrencyChanged(val currency : String) : CurrencyFormEvent()
}

sealed class CurrencyState{

    data class CurrencyError(val message: String):CurrencyState()
    object Clear: CurrencyState()

}

sealed class CartFormEvent{
    data class ItemNameChanged(val itemName: String):CartFormEvent()
    data class ItemPriceChanged(val ItemPrice: String):CartFormEvent()

    object Submit:CartFormEvent()
}

sealed class CartState{
    data class Success(val item : ShoppingItem):CartState()

    data class ItemNameError(val message : String):CartState()
    data class ItemPriceError(val message : String):CartState()

    object Clear: CartState()
}