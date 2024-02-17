package com.example.saviri.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.ShoppingItem
import com.example.saviri.domain.usecases.ValidateCart
import com.example.saviri.network.ApiClient
import com.example.saviri.network.models.Parent
import com.example.saviri.repository.api.ApiRepoeImpl
import com.example.saviri.repository.shoppinglist.ShoppingListRepoImpl
import com.example.saviri.util.Conversion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val shopinglistrespo : ShoppingListRepoImpl,
    private val  validateCart: ValidateCart = ValidateCart(),
    private val repository: ApiRepoeImpl = ApiRepoeImpl(ApiClient),
) : ViewModel() {

    private var _state = MutableStateFlow(CartFormState())
    val state = _state.asStateFlow()


    private var cartValidationEventChannel = Channel<CartState>()
    val cartValidationChannel = cartValidationEventChannel.receiveAsFlow()

    private var _currencyState = MutableStateFlow(CurrencyFormState())
    val currencyState = _currencyState.asStateFlow()

    private val mutableList : MutableList<ShoppingItem> = arrayListOf()
    private var _stateCartItems = MutableStateFlow(mutableList)
    val stateCartItems = _stateCartItems.asStateFlow()

    private var _addCartEventChannel = Channel<AddCartState>()
    val addCartEventChannel = _addCartEventChannel.receiveAsFlow()

    private var _parent = MutableStateFlow<Parent?>(Parent(success = false,timestamp = null,base = null,date = null,rates = null))
    private val parent = _parent.asStateFlow()

    private var _conversion = MutableStateFlow(Conversion(convertFrom = "", convertTo = ""))
    val conversion = _conversion.asStateFlow()

    private var _shoppingListId = MutableStateFlow("")
    val shoppinglistid = _shoppingListId.asStateFlow()

    private var _shoppingListName = MutableStateFlow("")
    val shoppinglistname = _shoppingListName.asStateFlow()

    private val validateCurrency = Channel<CurrencyState>()
    val currencyvalidationEvents = validateCurrency.receiveAsFlow()
    init {
        Log.d("TAG", "initializing: ")
        _stateCartItems.value = getList()
    }

    fun addItemsToCart(event: AddCartEvent) {
        when(event){
            is AddCartEvent.CartChanged -> {

                addToCart(event.item)
            }
            is AddCartEvent.AddAllItems -> {
                _stateCartItems.value += event.item
            }
            is AddCartEvent.EditCartITem -> {
                Log.d("todayt", "addItemsToCart: ${event.item}")
                updateCartItem(event.item)
            }
            else -> {}
        }
    }

    private fun updateCartItem(item: ShoppingItem) {

        viewModelScope.launch {
            if(item.key.isNullOrBlank()){
                val shoppingList_id:String = shopinglistrespo.insert(item,_conversion.value,_shoppingListName.value)
                _shoppingListId.value = shoppingList_id
            }else{
                shopinglistrespo.updateToExistingList(item,_shoppingListId.value)
            }
        }
    }

    private fun addToCart(item: ShoppingItem) {

        _stateCartItems.value += item
        viewModelScope.launch {
            if(_shoppingListId.value == ""){
                val shoppingList_id:String = shopinglistrespo.insert(item,_conversion.value,_shoppingListName.value)
                _shoppingListId.value = shoppingList_id
            }else{
                shopinglistrespo.insertToExistingList(item,_shoppingListId.value)
            }
            _addCartEventChannel.send(AddCartState.CartState(_stateCartItems.value.size))
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
            else -> {}
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
            else -> {}
        }
    }

    fun getLatestCurrency(
        conversion: Conversion,
        total: Double,
        shoppingid: String,
        shoppingListName: String
    ) {
        _conversion.value = _conversion.value.copy(convertTo = conversion.convertTo, convertFrom = conversion.convertFrom)
        _shoppingListId.value = shoppingid
        _shoppingListName.value = shoppingListName
        Log.d("asd1111111asdadsdas", "getLatestCurrency: $conversion $total $shoppingid $shoppingListName ")
        Log.d("Idonotknow", "getLatestCurrency: ${_shoppingListId.value}")
        viewModelScope.launch {
            val results = repository.getBaseCountries()
            if(results.success == true){

                Log.d("{}{}{}{}{}{}{}", "getLatestCurrency: results ${results.success}")
                _parent.value = _parent.value?.copy(results.success,results.timestamp,results.base,results.date,results.rates)
                var listCurrency = _parent.value!!.rates!!.getAllRates()

                var filteredlistTo = listCurrency.filter { value->


                    var remotekey  = value.split("=")
                    var localkeyTo  = conversion.convertTo.split("=")

                    remotekey[0] == localkeyTo[0]

                }
                var filteredlistFrom = listCurrency.filter { value->


                    var remotekey  = value.split("=")
                    var localkeyFrom  = conversion.convertFrom.split("=")

                    remotekey[0] == localkeyFrom[0]

                }

                Log.d("TAG---------------", "getLatestCurrency: $filteredlistTo")
                Log.d("TAG---------------", "getLatestCurrency: $filteredlistFrom")
                _currencyState.value = _currencyState.value.copy(foreignCurrencyToEuro = filteredlistFrom[0], homeCurrencyToEuro = filteredlistTo[0])
                currencyConversion(total)
            }
        }


    }
    private fun currencyConversion(total: Double) {

        val foreigncurr =  _currencyState.value.foreignCurrencyToEuro.split("=").last()
        val homecurr = _currencyState.value.homeCurrencyToEuro.split("=").last()


        val afterconversion = total / foreigncurr.toDouble() * homecurr.toDouble()

        viewModelScope.launch {
            validateCurrency.send(CurrencyState.CurrencyConverted(afterconversion))
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
                    1,
                    null
                )
            ))
        }

    }


    companion object {
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = ApiRepoeImpl(ApiClient)

                var databaseReference :DatabaseReference = Firebase.database.reference
                val shoppingRepo = ShoppingListRepoImpl(FirebaseAuth.getInstance(),databaseReference)
                return HomeViewModel(shopinglistrespo = shoppingRepo) as T
            }
        }
    }
    fun getList(): MutableList<ShoppingItem> {
        return mutableListOf(

        )
    }
}

data class CartFormState(
    val itemName : String = "",
    val itemNameError : String?=null,
    val itemPrice : String="",
    val itemPriceError:String?=null,
    val itemkey : String?=null
)

data class CurrencyFormState(
    val currencyRate : String = "",
    val foreignCurrencyToEuro : String = "",
    val homeCurrencyToEuro : String = "",
    val currencyRateError : String ?= null,
    val hasError : Boolean = false)

sealed class AddCartEvent{
    data class CartChanged(val item:ShoppingItem) : AddCartEvent()

    data class AddAllItems(val item: Array<ShoppingItem>) :AddCartEvent()

    data class EditCartITem(val item:ShoppingItem) : AddCartEvent()
}

sealed class AddCartState{
    data class CartState(val position:Int) : AddCartState()
}
sealed class CurrencyFormEvent{
    data class CurrencyChanged(val currency : String) : CurrencyFormEvent()

    object submit : CurrencyFormEvent()
}

sealed class CurrencyState{

    data class CurrencyConverted(val convertedData:Double):CurrencyState()
    data class CurrencyError(val message: String):CurrencyState()
    object Clear: CurrencyState()

}

sealed class CartFormEvent{
    data class ItemNameChanged(val itemName: String):CartFormEvent()
    data class ItemPriceChanged(val ItemPrice: String):CartFormEvent()

    data class ItemKeyChanged(val ItemPrice: String):CartFormEvent()

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