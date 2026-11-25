package com.example.adt_nabati.data.model

// Satu baris tabel mobile.konsumsi (riwayat catat konsumsi)
// Nilai gizi disimpan per bungkus, total dihitung dari jumlah bungkus
data class KonsumsiModel(
    val id: Long? = null,
    val user_id: Long,
    val nama_produk: String,
    val jumlah: Int,
    val gula: Int,
    val natrium: Int,
    val lemak: Int,
    val created_at: String? = null
)
