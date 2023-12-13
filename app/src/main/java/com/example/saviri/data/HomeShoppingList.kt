package com.example.saviri.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HomeShoppingList(val id : String,val name:String) : Parcelable