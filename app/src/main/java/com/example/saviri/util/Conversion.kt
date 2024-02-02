package com.example.saviri.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Conversion(val convertFrom:String,val convertTo:String) : Parcelable {
    constructor() : this("","")
}
