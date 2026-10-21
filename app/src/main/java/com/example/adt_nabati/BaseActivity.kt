package com.example.adt_nabati

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivityBaseBinding

class BaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "BaseActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // systemBars.bottom diganti 0 agar Bottom Navigation tidak berjarak di bagian bawah
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    Toast.makeText(this, "Beranda Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.jelajah -> {
                    Toast.makeText(this, "Jelajah Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.akun -> {
                    Toast.makeText(this, "Akun Clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false // return false jika item tidak ada yang di klik
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "BaseActivity dihapus dari stack")
    }
}
