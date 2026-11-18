package com.example.adt_nabati.Beranda

// Model data satu produk di Katalog, isinya sama dengan data yang dikirim ke SnackDetailActivity
data class ProdukModel(
    val nama: String,
    val deskripsi: String,
    val gambar: Int,
    val kategori: String,
    val takaran: Int,
    val energi: Int,
    val lemak: Int,
    val protein: Int,
    val karbo: Int,
    val gula: Int,
    val natrium: Int
)
