package com.example.saviri.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingItem(val name:String, val price : Double, val quantity :Int):Parcelable