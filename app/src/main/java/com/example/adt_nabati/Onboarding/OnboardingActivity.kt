package com.example.adt_nabati.Onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.AuthActivity
import com.example.adt_nabati.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "OnboardingActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tiga halaman panduan dipasang ke ViewPager2
        val halamanList = listOf(OnboardGiziFragment(), OnboardCatatFragment(), OnboardBatasFragment())
        val adapter = OnboardingAdapter(this, halamanList)
        binding.pagerOnboarding.adapter = adapter

        binding.dotOnboarding.attachTo(binding.pagerOnboarding)

        // Tandai panduan sudah dilihat, lalu ke Login jika belum masuk
        binding.btnMulai.setOnClickListener {
            val appPref = getSharedPreferences("app_pref", MODE_PRIVATE)
            val editor = appPref.edit()
            editor.putBoolean("isOnboardingSelesai", true)
            editor.apply()
            Log.e("Onboarding", "isOnboardingSelesai disimpan true")

            val userPref = getSharedPreferences("user_pref", MODE_PRIVATE)
            if (!userPref.getBoolean("isLogin", false)) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "OnboardingActivity dihapus dari stack")
    }
}
