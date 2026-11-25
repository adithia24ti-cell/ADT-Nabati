package com.example.adt_nabati.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseApiClient {
    private const val BASE_URL = "https://qvytggnuyduyytevdfqe.supabase.co/rest/v1/"

    // Publishable key dari Project Settings > API Keys
    const val API_KEY = "sb_publishable_1RO9cQlYiKvhX21qKNtQpA_BxhftH8I"

    val apiService: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}
