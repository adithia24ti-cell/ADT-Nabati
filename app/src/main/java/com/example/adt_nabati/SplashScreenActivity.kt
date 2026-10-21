package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.adt_nabati.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "SplashScreenActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Kode ini harus selalu dipanggil saat butuh akses "user_pref"
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)

        lifecycleScope.launch {
            delay(2000) // simulasi pengambilan data selama 2 detik

            // Pengecekan isLogin dipindahkan ke SplashScreen
            val isLogin = sharedPref.getBoolean("isLogin", false)
            Log.e("Cek Login", "isLogin = $isLogin")

            if (isLogin) {
                // Sudah login -> langsung ke BaseActivity (halaman utama dengan Bottom Navigation)
                val intent = Intent(this@SplashScreenActivity, BaseActivity::class.java)
                startActivity(intent)
            } else {
                // Belum login -> ke halaman login (AuthActivity)
                val intent = Intent(this@SplashScreenActivity, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "SplashScreenActivity dihapus dari stack")
    }
}