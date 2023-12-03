package com.example.saviri.network.models

import com.google.gson.annotations.SerializedName

data class Parent (
    @SerializedName("success") var success   : Boolean?,
    @SerializedName("timestamp") var timestamp : Int?,
    @SerializedName("base") var base      : String?,
    @SerializedName("date") var date      : String?,
    @SerializedName("rates") var rates     : Rates?   = Rates()
)