package com.example.saviri.network.models

import com.google.gson.annotations.SerializedName

data  class Countries (
    @SerializedName("success") var success : Boolean? = null,
    @SerializedName("symbols") var symbols : Symbols? = Symbols()
    )