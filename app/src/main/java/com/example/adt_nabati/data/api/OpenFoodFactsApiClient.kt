package com.example.adt_nabati.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenFoodFactsApiClient {
    // Basis data pangan terbuka, tidak perlu API key
    private const val BASE_URL = "https://world.openfoodfacts.org/api/v2/"

    val apiService: OpenFoodFactsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenFoodFactsApiService::class.java)
    }
}
