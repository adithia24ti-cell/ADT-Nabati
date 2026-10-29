package com.example.adt_nabati

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.adt_nabati.Akun.AkunFragment
import com.example.adt_nabati.Beranda.BerandaFragment
import com.example.adt_nabati.Jelajah.JelajahFragment
import com.example.adt_nabati.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding // 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "BaseActivity dibuat pertama kali")
        enableEdgeToEdge()

        binding = ActivityBaseBinding.inflate(layoutInflater) // 2
        setContentView(binding.root)  // 3

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        replaceFragment(BerandaFragment())

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    Log.e("Bottom Nav", "Menu Beranda dipilih")
                    replaceFragment(BerandaFragment())
                    true
                }
                R.id.jelajah -> {
                    Log.e("Bottom Nav", "Menu Jelajah dipilih")
                    replaceFragment(JelajahFragment())
                    true
                }
                R.id.akun -> {
                    Log.e("Bottom Nav", "Menu Akun dipilih")
                    replaceFragment(AkunFragment())
                    true
                }
                else -> false // return false jika item tidak ada yang di klik
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            //.addToBackStack(null) -> dinonaktifkan agar saat back langsung keluar aplikasi
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "BaseActivity dihapus dari stack")
    }
}
