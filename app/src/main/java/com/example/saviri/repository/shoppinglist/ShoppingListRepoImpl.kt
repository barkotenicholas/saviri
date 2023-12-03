package com.example.saviri.repository.shoppinglist

import android.util.Log
import com.example.saviri.data.Resource
import com.example.saviri.data.ShoppingItem
import com.example.saviri.data.ShoppingListName
import com.example.saviri.util.Conversion
import com.example.saviri.util.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ShoppingListRepoImpl(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase:DatabaseReference
) :ShoppingListRepo  {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun insert(
        item: ShoppingItem,
        conversion: Conversion,
        shoppingListName: String
    ): String {

        val shoppingListId = firebaseDatabase.push().key

        val itemref = firebaseDatabase.child(shoppingListId.toString()).push().key

        firebaseDatabase.child(shoppingListId.toString()).child("items").child(itemref.toString()).setValue(item)
        firebaseDatabase.child(shoppingListId.toString()).child("conversion").setValue(conversion)
        firebaseDatabase.child(shoppingListId.toString()).child("name").setValue(shoppingListName)

        return shoppingListId.toString()
    }

    init {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference(currentUser!!.uid).child("ShoppingList")
    }



    override suspend fun insertToExistingList(item: ShoppingItem, shoppingRefernce: String) {
        val itemref = firebaseDatabase.child(shoppingRefernce).push().key

        Log.d("|ASasASasASas" ,"insertToExistingList: $shoppingRefernce")
        firebaseDatabase.child(shoppingRefernce).child("items").child(itemref.toString()).setValue(item)
    }

    override suspend fun createShoppingList(shoppingListName: String,conversion: Conversion) {


        val shoppingListId = firebaseDatabase.push().key
        firebaseDatabase.child(shoppingListId.toString()).setValue(shoppingListName)
        firebaseDatabase.child(shoppingListId.toString()).setValue(conversion)

    }

}