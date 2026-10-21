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

        // Kode ini harus selalu dipanggil saat butuh akses "user_pref"
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            // Ambil isi EditText
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()
            Log.e("Cek Login", "Username: $username")

            // Autentikasi: username tidak boleh kosong dan harus sama dengan password
            if (username.isNotEmpty() && username == password) {
                // Berhasil -> simpan isLogin = true dan username
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", username)
                editor.apply()
                Log.e("Info Login", "Login berhasil, isLogin disimpan true")

                // Ke BaseActivity (halaman utama dengan Bottom Navigation)
                val intent = Intent(this, BaseActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Gagal -> tampilkan AlertDialog
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