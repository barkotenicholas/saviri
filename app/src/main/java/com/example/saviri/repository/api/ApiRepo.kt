package com.example.saviri.repository.api

import com.example.saviri.network.ApiClient
import retrofit2.Retrofit

interface ApiRepo {

    suspend fun getSupportedCountries()

    suspend fun getBaseCountries()

}