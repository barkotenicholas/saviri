package com.example.saviri.repository.api

import android.util.Log
import com.example.saviri.network.ApiClient
import com.example.saviri.network.models.Countries
import kotlin.math.log


class ApiRepoeImpl(private val apiClient: ApiClient) : ApiRepo {
    override suspend fun getSupportedCountries(): Countries {
        return runCatching {
            val response = apiClient.getInterface().getSupportedCountries()

            if (response.isSuccessful) {
                return response.body()!!
            } else {
                throw Exception("Unsuccessful response: ${response.code()} - ${response.message()}")
            }
        }.getOrElse {
            Log.d("TAG", "Error: $it")
            // Return a default value or handle the error as needed
            // For example, you can return an empty Countries object
            Countries(success = false,symbols = null) // Replace this with an appropriate default value
        }
    }

    override suspend fun getBaseCountries() {
        TODO("Not yet implemented")
    }
}