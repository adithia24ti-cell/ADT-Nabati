package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivityAuthBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString()
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
            } else if (username == password) {
                // Login berhasil jika username sama dengan password
                // Simpan status login dan username
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()
                Log.e("Info Login", "Login berhasil, isLogin disimpan true")

                val intent = Intent(this, BaseActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.e("Info Login", "Login gagal")
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialog_login_title)
                    .setMessage(R.string.dialog_login_message)
                    .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "AuthActivity dihapus dari stack")
    }
}