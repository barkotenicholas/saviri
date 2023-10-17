package com.example.saviri.repository.api

import com.example.saviri.network.ApiClient


class ApiRepoeImpl(private val apiClient: ApiClient) : ApiRepo {
    override suspend fun getSupportedCountries() {
        return try {

            var response = apiClient.getInterface().getSupportedCountries().execute()





        }catch (e : Exception){

        }
    }

    override suspend fun getBaseCountries() {
        TODO("Not yet implemented")
    }
}