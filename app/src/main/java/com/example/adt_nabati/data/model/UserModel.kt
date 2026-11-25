package com.example.adt_nabati.data.model

// Satu baris tabel mobile.users, nama properti sama dengan nama kolom
// id dibiarkan null saat register agar diisi otomatis oleh Supabase
data class UserModel(
    val id: Long? = null,
    val username: String,
    val email: String,
    val password: String
)
