package com.example.saviri.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingItem(var name:String, var price : Double, var quantity :Int,var key:String?):Parcelable{
    constructor():this("",0.0,0,"")
    constructor(name: String, price: Double, quantity: Int) : this()

}