package com.example.saviri.network

import com.example.saviri.network.models.Countries
import retrofit2.Call
import retrofit2.http.GET

interface CurrencyApi {

    @GET("latest")
    fun convertApi()

    @GET("symbols")
    fun getSupportedCountries():Call<Countries>

}