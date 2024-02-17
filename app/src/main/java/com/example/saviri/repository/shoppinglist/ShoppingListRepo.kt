package com.example.saviri.repository.shoppinglist

import com.example.saviri.data.HomeShoppingList
import com.example.saviri.data.ShoppingItem
import com.example.saviri.data.ShoppingListName
import com.example.saviri.util.Conversion
import com.google.firebase.auth.FirebaseUser

interface ShoppingListRepo {

    val currentUser:FirebaseUser?

    suspend fun insert(item:ShoppingItem,conversion: Conversion,shoppingListName: String):String

    suspend fun insertToExistingList(item:ShoppingItem,shoppingRefernce:String)

    suspend fun createShoppingList(shoppingListName:String,conversion: Conversion)

    suspend fun getUserShoppingList():List<HomeShoppingList>

    suspend fun updateToExistingList(item:ShoppingItem,shoppingRefernce:String)
}