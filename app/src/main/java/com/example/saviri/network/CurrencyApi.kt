package com.example.saviri.network

import com.example.saviri.network.models.Countries
import com.example.saviri.network.models.Parent
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface CurrencyApi {

    @POST("latest")
    suspend fun convertApi() :Response<Parent>

    @POST("symbols")
    suspend fun getSupportedCountries():Response<Countries>

}