package com.example.adt_nabati.data.model

// Satu baris tabel mobile.favorit (produk favorit beserta catatan pribadi)
data class FavoritModel(
    val id: Long? = null,
    val user_id: Long,
    val nama_produk: String,
    val catatan: String,
    val created_at: String? = null
)
