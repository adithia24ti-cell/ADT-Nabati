package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.adt_nabati.data.api.SupabaseApiClient
import com.example.adt_nabati.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "AuthActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            // Spasi di awal/akhir username dibuang
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString()
            Log.e("Cek Login", "Username: $username")

            // Hapus pesan error dari percobaan sebelumnya
            binding.tilUsername.error = null
            binding.tilPassword.error = null

            // Kolom kosong ditandai langsung di bawah input, tanpa dialog
            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) {
                    binding.tilUsername.error = getString(R.string.error_username_kosong)
                }
                if (password.isEmpty()) {
                    binding.tilPassword.error = getString(R.string.error_password_kosong)
                }
                Log.e("Info Login", "Login gagal, ada kolom kosong")
            } else {
                cekLogin(username, password)
            }
        }

        binding.textDaftar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Cari user di tabel users
    private fun cekLogin(username: String, password: String) {
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.login("eq.$username", "eq.$password")
                val hasil = response.body()
                Log.e("Supabase", "Login $username, kode respon: ${response.code()}")

                if (!response.isSuccessful || hasil == null) {
                    // Server menolak request
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@AuthActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                } else if (hasil.isNotEmpty()) {
                    // Simpan status login
                    val user = hasil[0]
                    val editor = sharedPref.edit()
                    editor.putBoolean("isLogin", true)
                    editor.putString("username", user.username)
                    editor.putLong("userId", user.id ?: 0L)
                    editor.apply()
                    Log.e("Info Login", "Login berhasil, userId ${user.id} disimpan")

                    val intent = Intent(this@AuthActivity, BaseActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("Info Login", "Login gagal, username atau password salah")
                    MaterialAlertDialogBuilder(this@AuthActivity)
                        .setTitle(R.string.dialog_login_title)
                        .setMessage(R.string.dialog_login_message)
                        .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Login gagal: ${e.message}")
                Toast.makeText(this@AuthActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
            binding.btnLogin.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "AuthActivity dihapus dari stack")
    }
}