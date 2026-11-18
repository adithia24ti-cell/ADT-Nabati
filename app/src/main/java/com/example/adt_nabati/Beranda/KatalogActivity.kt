package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.ActivityKatalogBinding
import com.google.android.material.chip.Chip

class KatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKatalogBinding

    private lateinit var produkList: List<ProdukModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "KatalogActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityKatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_katalog)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // Data dibuat di onCreate agar getString sudah bisa dipakai
        produkList = listOf(
            ProdukModel(getString(R.string.nama_richeese), getString(R.string.desc_richeese),
                R.drawable.nabati_richeese, getString(R.string.chip_keju), 37, 190, 10, 2, 24, 11, 70),
            ProdukModel(getString(R.string.nama_richoco), getString(R.string.desc_richoco),
                R.drawable.nabati_richoco, getString(R.string.chip_cokelat), 37, 185, 10, 2, 23, 12, 55),
            ProdukModel(getString(R.string.nama_rolls), getString(R.string.desc_rolls),
                R.drawable.nabati_rolls, getString(R.string.chip_keju), 33, 170, 9, 2, 21, 10, 65)
        )

        tampilkanProduk(getString(R.string.chip_semua))

        // Filter daftar produk sesuai chip kategori yang dipilih
        binding.chipGroupKategori.setOnCheckedStateChangeListener { group, checkedIds ->
            val idChipTerpilih = checkedIds.firstOrNull()

            if (idChipTerpilih != null) {
                val chip = group.findViewById<Chip>(idChipTerpilih)
                Toast.makeText(this, getString(R.string.toast_filter, chip.text), Toast.LENGTH_SHORT).show()
                tampilkanProduk(chip.text.toString())
            } else {
                // Chip terpilih dilepas, tampilkan semua produk lagi
                tampilkanProduk(getString(R.string.chip_semua))
            }
        }
    }

    // Pasang adapter baru berisi produk dengan kategori tertentu
    private fun tampilkanProduk(kategori: String) {
        val hasilFilter = mutableListOf<ProdukModel>()
        for (produk in produkList) {
            if (kategori == getString(R.string.chip_semua) || produk.kategori == kategori) {
                hasilFilter.add(produk)
            }
        }

        val adapter = ProdukAdapter(this, hasilFilter)
        binding.listProduk.adapter = adapter

        supportActionBar?.subtitle = getString(R.string.fmt_jumlah_produk, hasilFilter.size)
        Log.e("Katalog", "Filter $kategori: ${hasilFilter.size} produk")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "KatalogActivity dihapus dari stack")
    }
}
