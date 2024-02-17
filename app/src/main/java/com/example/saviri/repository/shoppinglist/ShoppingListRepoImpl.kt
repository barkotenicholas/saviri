package com.example.saviri.repository.shoppinglist

import android.util.Log
import com.example.saviri.data.HomeShoppingList
import com.example.saviri.data.ShoppingItem
import com.example.saviri.data.ShoppingListName
import com.example.saviri.util.Conversion
import com.example.saviri.util.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlin.math.log

class ShoppingListRepoImpl(
    private val firebaseAuth: FirebaseAuth,
    private var firebaseDatabase:DatabaseReference
) :ShoppingListRepo {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun insert(
        item: ShoppingItem,
        conversion: Conversion,
        shoppingListName: String
    ): String {

        val shoppingListId = firebaseDatabase.push().key

        val itemref = firebaseDatabase.child(shoppingListId.toString()).push().key

        firebaseDatabase.child(shoppingListId.toString()).child("items").child(itemref.toString())
            .setValue(item)
        firebaseDatabase.child(shoppingListId.toString()).child("conversion").setValue(conversion)
        firebaseDatabase.child(shoppingListId.toString()).child("name").setValue(shoppingListName)

        return shoppingListId.toString()
    }

    init {
        firebaseDatabase =
            FirebaseDatabase.getInstance().getReference(currentUser!!.uid).child("ShoppingList")
    }


    override suspend fun insertToExistingList(item: ShoppingItem, shoppingRefernce: String) {
        val itemref = firebaseDatabase.child(shoppingRefernce).push().key

        Log.d("|ASasASasASas", "insertToExistingList: $shoppingRefernce")
        var new = ShoppingItem(item.name,item.price,item.quantity)
        firebaseDatabase.child(shoppingRefernce).child("items").child(itemref.toString())
            .setValue(new)
    }
    override suspend fun updateToExistingList(item: ShoppingItem, shoppingRefernce: String) {
        var update = ShoppingItem(item.name,item.price,item.quantity)
        firebaseDatabase.child(shoppingRefernce).child("items").child(item.key.toString())
            .setValue(update)
    }


    override suspend fun createShoppingList(shoppingListName: String, conversion: Conversion) {


        val shoppingListId = firebaseDatabase.push().key
        firebaseDatabase.child(shoppingListId.toString()).setValue(shoppingListName)
        firebaseDatabase.child(shoppingListId.toString()).setValue(conversion)

    }

    override suspend fun getUserShoppingList(): List<HomeShoppingList> {

        var data = firebaseDatabase.get().await()
        Log.d("TAG", "getUserShoppingList: $data")
        var shopingList: List<HomeShoppingList> = emptyList()
        for (d in data.children) {

            var data1 = firebaseDatabase.child(d.key.toString()).get().await()
            var data2 = this.getShoppingData(d.key.toString(), data1)
            var data3 = this.getShoppingConvertion(d.key.toString(), data1)
            var ig = this.getShoppingList(d.key.toString(), data1)
//            shopingList = shopingList + data2
            shopingList = shopingList + HomeShoppingList(
                data1.key.toString(), data2.name, conversion = data3,
                shoppinglist = ig
            )
        }

        return shopingList

    }


    private suspend fun getShoppingData(data11: String, data1: DataSnapshot): ShoppingListName {

        for (snap in data1.children) {

            if (snap.key == "name") {
                val data = firebaseDatabase.child(data11).child(snap.key.toString()).get().await()

                if (data != null) {
                    return ShoppingListName(data.value as String)
                }

            }

        }
        return ShoppingListName("")

    }

    private suspend fun getShoppingConvertion(data11: String, data1: DataSnapshot): Conversion {

        for (snap in data1.children) {

            if (snap.key == "conversion") {
                val data = firebaseDatabase.child(data11).child(snap.key.toString()).get().await()

                if (data != null) {
                    Log.d("TAGAASA", "getShoppingConvertion: ${data}")
                    return data.getValue(Conversion::class.java)!!
                }

            }

        }
        return Conversion("", "")

    }

    private suspend fun getShoppingList(data11: String, data1: DataSnapshot): List<ShoppingItem> {

        var shopingList: MutableList<ShoppingItem> = mutableListOf()

        for (snap in data1.children) {

            if (snap.key == "items") {
                val data = firebaseDatabase.child(data11).child(snap.key.toString()).get().await()

                if (data != null) {
                    Log.d("TAGwerAASA", "getShoppinasdasdadsdasdgConvertion: ${data}")
//                    return data.getValue(Conversion::class.java)!!
                    var dat = data.children
                    dat.forEach {
                        Log.d(
                            "TAG*--*-*-*-*--*",
                            "getShoppingList----------------------------------:  ${it.key} "
                        )

                        var shopingItem = it.getValue(ShoppingItem::class.java)!!
                        shopingList.add(shopingItem.copy(key = it.key))
                    }
                }

            }

        }
        Log.d("TAGpp", "getShoppingList: all shopping is ${shopingList}")
        return shopingList

    }

}