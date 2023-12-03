package com.example.saviri.ui.AllShoppingList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.saviri.repository.shoppinglist.ShoppingListRepoImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AllShoppingViewModel(
    private val shopinglistrespo : ShoppingListRepoImpl,
    ) : ViewModel() {






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

}