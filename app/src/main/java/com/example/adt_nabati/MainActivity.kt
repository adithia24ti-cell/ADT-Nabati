package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivityMainBinding
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "MainActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Card produk -> kirim data ke SnackDetailActivity
        binding.cardRicheese.setOnClickListener {
            bukaDetail(getString(R.string.nama_richeese), getString(R.string.desc_richeese),
                R.drawable.nabati_richeese, 37, 190, 10, 2, 24, 11, 70)
        }

        binding.cardRichoco.setOnClickListener {
            bukaDetail(getString(R.string.nama_richoco), getString(R.string.desc_richoco),
                R.drawable.nabati_richoco, 37, 185, 10, 2, 23, 12, 55)
        }

        binding.cardRolls.setOnClickListener {
            bukaDetail(getString(R.string.nama_rolls), getString(R.string.desc_rolls),
                R.drawable.nabati_rolls, 33, 170, 9, 2, 21, 10, 65)
        }

        binding.btnWebView.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }
    }

    // Intent + putExtra: kirim data produk ke SnackDetailActivity
    private fun bukaDetail(
        nama: String, deskripsi: String, gambar: Int,
        takaran: Int, energi: Int, lemak: Int, protein: Int,
        karbo: Int, gula: Int, natrium: Int
    ) {
        val intent = Intent(this, SnackDetailActivity::class.java)
        intent.putExtra("nama", nama)
        intent.putExtra("deskripsi", deskripsi)
        intent.putExtra("gambar", gambar)
        intent.putExtra("takaran", takaran)
        intent.putExtra("energi", energi)
        intent.putExtra("lemak", lemak)
        intent.putExtra("protein", protein)
        intent.putExtra("karbo", karbo)
        intent.putExtra("gula", gula)
        intent.putExtra("natrium", natrium)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        Log.e("onStart", "onStart: MainActivity terlihat di layar")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "MainActivity dihapus dari stack")
    }
}