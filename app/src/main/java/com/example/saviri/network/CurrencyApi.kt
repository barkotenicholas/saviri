package com.example.saviri.network

import com.example.saviri.network.models.Countries
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface CurrencyApi {

    @GET("latest")
    fun convertApi()

    @POST("symbols")
    suspend fun getSupportedCountries():Response<Countries>

}