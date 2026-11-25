package com.example.adt_nabati

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.adt_nabati.data.api.SupabaseApiClient
import com.example.adt_nabati.data.model.UserModel
import com.example.adt_nabati.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "RegisterActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnDaftar.setOnClickListener {
            val username = binding.edtUsernameDaftar.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPasswordDaftar.text.toString()

            // Hapus pesan error dari percobaan sebelumnya
            binding.tilUsernameDaftar.error = null
            binding.tilEmail.error = null
            binding.tilPasswordDaftar.error = null

            // Validasi sederhana sebelum data dikirim ke Supabase
            var valid = true
            if (username.isEmpty()) {
                binding.tilUsernameDaftar.error = getString(R.string.error_username_kosong)
                valid = false
            }
            if (!email.contains("@")) {
                binding.tilEmail.error = getString(R.string.error_email)
                valid = false
            }
            if (password.length < 4) {
                binding.tilPasswordDaftar.error = getString(R.string.error_password_pendek)
                valid = false
            }

            if (valid) {
                daftarUser(UserModel(username = username, email = email, password = password))
            } else {
                Log.e("Supabase", "Register dibatalkan, isian belum valid")
            }
        }

        binding.textKeLogin.setOnClickListener {
            finish()
        }
    }

    private fun daftarUser(user: UserModel) {
        binding.btnDaftar.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.register(user)
                Log.e("Supabase", "Register ${user.username}, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, getString(R.string.toast_register_berhasil, user.username), Toast.LENGTH_LONG).show()
                    // Kembali ke halaman login agar user masuk dengan lagi
                    finish()
                } else if (response.code() == 409) {

                    binding.tilUsernameDaftar.error = getString(R.string.error_username_dipakai)
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@RegisterActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Register gagal: ${e.message}")
                Toast.makeText(this@RegisterActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
            binding.btnDaftar.isEnabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "RegisterActivity dihapus dari stack")
    }
}
