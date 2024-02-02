package com.example.saviri.data

import android.os.Parcelable
import com.example.saviri.util.Conversion
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeShoppingList(
    val id: String,
    val name:String,
    val conversion: Conversion,
    val shoppinglist: List<ShoppingItem>
) : Parcelable