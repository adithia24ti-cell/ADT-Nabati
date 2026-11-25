package com.example.adt_nabati.data.api

import com.example.adt_nabati.data.model.FavoritModel
import com.example.adt_nabati.data.model.KonsumsiModel
import com.example.adt_nabati.data.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

    interface SupabaseApiService {

    // Register: tambah baris baru ke tabel users
    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @POST("users")
    suspend fun register(@Body user: UserModel): Response<Unit>

    // Login: cari user dengan username dan password yang cocok
    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Accept-Profile: mobile")
    @GET("users")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<List<UserModel>>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Accept-Profile: mobile")
    @GET("konsumsi")
    suspend fun getKonsumsi(
        @Query("user_id") userId: String,
        @Query("order") order: String
    ): Response<List<KonsumsiModel>>

    // Prefer return=representation membuat Supabase mengirim balik baris baru beserta id-nya
    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile", "Prefer: return=representation")
    @POST("konsumsi")
    suspend fun tambahKonsumsi(@Body konsumsi: KonsumsiModel): Response<List<KonsumsiModel>>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @PATCH("konsumsi")
    suspend fun ubahKonsumsi(
        @Query("id") id: String,
        @Body data: Map<String, Int>
    ): Response<Unit>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @DELETE("konsumsi")
    suspend fun hapusKonsumsi(@Query("id") id: String): Response<Unit>

    // Daftar favorit milik satu user
    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Accept-Profile: mobile")
    @GET("favorit")
    suspend fun getFavorit(
        @Query("user_id") userId: String,
        @Query("order") order: String
    ): Response<List<FavoritModel>>

    // Cek apakah produk sudah ada di favorit user, agar tidak tersimpan dua kali
    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Accept-Profile: mobile")
    @GET("favorit")
    suspend fun cekFavorit(
        @Query("user_id") userId: String,
        @Query("nama_produk") namaProduk: String
    ): Response<List<FavoritModel>>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @POST("favorit")
    suspend fun tambahFavorit(@Body favorit: FavoritModel): Response<Unit>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @PATCH("favorit")
    suspend fun ubahFavorit(
        @Query("id") id: String,
        @Body data: Map<String, String>
    ): Response<Unit>

    @Headers("apikey: ${SupabaseApiClient.API_KEY}", "Content-Profile: mobile")
    @DELETE("favorit")
    suspend fun hapusFavorit(@Query("id") id: String): Response<Unit>
}
