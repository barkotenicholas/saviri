package com.example.saviri.ui.AllShoppingList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.data.HomeShoppingList
import com.example.saviri.data.ShoppingItem
import com.example.saviri.repository.shoppinglist.ShoppingListRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AllShoppingViewModel(
    private val shopinglistrespo : ShoppingListRepoImpl,
    ) : ViewModel() {

    private val loadingEventChannel = Channel<AllShoppingState>()
    val loadingEvent = loadingEventChannel.receiveAsFlow()

    private val mutableList : MutableList<HomeShoppingList> = arrayListOf()
    private var _stateCartItems = MutableStateFlow(mutableList)
    val stateCartItems = _stateCartItems.asStateFlow()

    suspend fun loadData(){
        viewModelScope.launch {

            loadingEventChannel.send(AllShoppingState.Success(true,"loading"))
        }

        val data =  shopinglistrespo.getUserShoppingList()
        Log.d("TAG", "loadData: loaded data $data")
        _stateCartItems = MutableStateFlow(arrayListOf())

        _stateCartItems.value = (_stateCartItems.value + data) as MutableList<HomeShoppingList>

        viewModelScope.launch {
            loadingEventChannel.send(AllShoppingState.ReceivedData(_stateCartItems.value))
        }
    }

    companion object{
        val Factory : ViewModelProvider.Factory = object : ViewModelProvider.Factory{
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

                val database = Firebase.database.reference
                val auth = FirebaseAuth.getInstance()

                val shoppingListRepo = ShoppingListRepoImpl(auth,database)
                return AllShoppingViewModel(shoppingListRepo) as T
            }
        }
    }

    sealed class AllShoppingState{
        object Loading : AllShoppingState()

        data class Success(val loading:Boolean,val message:String) :AllShoppingState()
        data class ReceivedData(var mutableList : MutableList<HomeShoppingList>):AllShoppingState()
    }

}