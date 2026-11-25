package com.example.adt_nabati.data.api

import com.example.adt_nabati.data.model.ProdukDuniaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface OpenFoodFactsApiService {
    // Open Food Facts meminta setiap aplikasi menyebutkan namanya lewat User-Agent
    @Headers("User-Agent: ADT-Nabati/1.0 (Android)")
    @GET("search")
    suspend fun cariProduk(
        @Query("brands_tags") merek: String,
        @Query("fields") kolom: String,
        @Query("page_size") jumlah: Int
    ): Response<ProdukDuniaResponse>
}
