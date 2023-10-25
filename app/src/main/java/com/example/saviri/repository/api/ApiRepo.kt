package com.example.saviri.repository.api

import com.example.saviri.network.ApiClient
import com.example.saviri.network.models.Countries
import retrofit2.Retrofit

interface ApiRepo {

    suspend fun getSupportedCountries(): Countries

    suspend fun getBaseCountries()

}