package com.example.saviri.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://api.exchangeratesapi.io/v1/"
    const val API_KEY = "e73ebdecad552de06eec147b8817abad"

    val okHttpClient: OkHttpClient =OkHttpClient().newBuilder().addInterceptor(RequestInterceptor).build()


    fun getClient():Retrofit=
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getInterface():CurrencyApi{
        return getClient().create(CurrencyApi::class.java)
    }
}

object RequestInterceptor: Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val originalHttp = chain.request().url()
        val newUrl = originalHttp
            .newBuilder()
            .addQueryParameter("access_key", ApiClient.API_KEY).
            build()
        println("Outgoing request to $newUrl")

        val newRequest = request.url(newUrl).build()

        return chain.proceed(newRequest)
    }

}