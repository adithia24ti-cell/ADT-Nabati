package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivityMainBinding

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

        binding.cardRicheese.setOnClickListener { bukaRicheese() }

        binding.cardRichoco.setOnClickListener {
            bukaDetail(
                nama = getString(R.string.nama_richoco),
                deskripsi = getString(R.string.desc_richoco),
                gambar = R.drawable.nabati_richoco,
                takaran = 37, energi = 185, lemak = 10, protein = 2,
                karbo = 23, gula = 12, natrium = 55
            )
        }

        binding.cardRolls.setOnClickListener {
            bukaDetail(
                nama = getString(R.string.nama_rolls),
                deskripsi = getString(R.string.desc_rolls),
                gambar = R.drawable.nabati_rolls,
                takaran = 33, energi = 170, lemak = 9, protein = 2,
                karbo = 21, gula = 10, natrium = 65
            )
        }
    }

    private fun bukaRicheese() {
        bukaDetail(
            nama = getString(R.string.nama_richeese),
            deskripsi = getString(R.string.desc_richeese),
            gambar = R.drawable.nabati_richeese,
            takaran = 37, energi = 190, lemak = 10, protein = 2,
            karbo = 24, gula = 11, natrium = 70
        )
    }

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