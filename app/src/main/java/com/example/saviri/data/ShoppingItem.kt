package com.example.saviri.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingItem(var name:String, var price : Double, var quantity :Int):Parcelable