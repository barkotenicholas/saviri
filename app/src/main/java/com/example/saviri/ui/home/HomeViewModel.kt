package com.example.saviri.ui.home

import android.text.TextUtils
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val  validateCart: ValidateCart = ValidateCart()
) : ViewModel() {

    private var _state = MutableStateFlow(CartFormState())
    val state = _state.asStateFlow()

    private var cartValidationEventChannel = Channel<CartState>()
    val cartValidationChannel = cartValidationEventChannel.receiveAsFlow()

    private var _currencyState = MutableStateFlow(CurrencyFormState())
    val currencyState = _currencyState.asStateFlow()

    private var _stateCartItems = MutableStateFlow(mutableListOf<ShoppingItem>())
    val stateCartItems = _stateCartItems.asStateFlow()

    private var _addCartEventChannel = Channel<AddCartState>()
    val addCartEventChannel = _addCartEventChannel.receiveAsFlow()


    init {
        _stateCartItems.value = getList()
    }

    fun addItemsToCart(event: AddCartEvent) {
        when(event){
            is AddCartEvent.CartChanged -> {
//                Log.d("TAG", "addItemsToCart:---------------------------${_stateCartItems.value.size}")
//                val current = _stateCartItems.value.toMutableList()
//                Log.d("TAG", "addItemsToCart: ------------------${current.size}")
//
//                val item = event.item
//                current.add(event.item)
//                Log.d("TAG", "addItemsToCart: ------------------${_stateCartItems.value.size}")
//                _stateCartItems.update {
//                    current
//                }
                _stateCartItems.value += event.item
                viewModelScope.launch {
                    _addCartEventChannel.send(AddCartState.CartState(1))
                }
            }
        }
    }

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
            CurrencyFormEvent.submit -> {
                validateCurrency()
            }
        }
    }

    private fun validateCurrency() {

        _currencyState.value = _currencyState.value.copy(hasError = false)



        val value = _currencyState.value.currencyRate
        if(value.isBlank()){
            _currencyState.value = _currencyState.value.copy(currencyRateError = "Input field is  blank")
            return
        }
        val number = _currencyState.value.currencyRate.toIntOrNull()
        val isInteger = number != null
        if(!isInteger){
            _currencyState.value = _currencyState.value.copy(currencyRateError = "Input field must only be digits")
            return
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
    fun getList(): MutableList<ShoppingItem> {
        return mutableListOf(
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8),
            ShoppingItem("beer", 20.0, 8)
        )
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
    val currencyRateError : String ?= null,
    val hasError : Boolean = false)

sealed class AddCartEvent{
    data class CartChanged(val item:ShoppingItem) : AddCartEvent()
}

sealed class AddCartState{
    data class CartState(val position:Int) : AddCartState()
}
sealed class CurrencyFormEvent{
    data class CurrencyChanged(val currency : String) : CurrencyFormEvent()

    object submit : CurrencyFormEvent()
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

fun <T> List<T>.mapButReplace(targetItem: T, newItem: T) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
    }
}