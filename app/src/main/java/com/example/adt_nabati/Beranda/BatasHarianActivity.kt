package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.ActivityBatasHarianBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BatasHarianActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBatasHarianBinding

    // Dibaca oleh fragment tab untuk menghitung persen tiap produk
    lateinit var produkList: List<ProdukModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "BatasHarianActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityBatasHarianBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_batas_harian)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        produkList = listOf(
            ProdukModel(getString(R.string.nama_richeese), getString(R.string.desc_richeese),
                R.drawable.nabati_richeese, getString(R.string.chip_keju), 37, 190, 10, 2, 24, 11, 70),
            ProdukModel(getString(R.string.nama_richoco), getString(R.string.desc_richoco),
                R.drawable.nabati_richoco, getString(R.string.chip_cokelat), 37, 185, 10, 2, 23, 12, 55),
            ProdukModel(getString(R.string.nama_rolls), getString(R.string.desc_rolls),
                R.drawable.nabati_rolls, getString(R.string.chip_keju), 33, 170, 9, 2, 21, 10, 65)
        )

        // Hitung produk berstatus tinggi (minimal 20% batas) untuk badge tab
        var tinggiGula = 0
        var tinggiNatrium = 0
        var tinggiLemak = 0
        for (produk in produkList) {
            if (produk.gula * 100 / 50 >= 20) tinggiGula++
            if (produk.natrium * 100 / 2000 >= 20) tinggiNatrium++
            if (produk.lemak * 100 / 67 >= 20) tinggiLemak++
        }
        Log.e("Batas Harian", "Produk tinggi: gula $tinggiGula, natrium $tinggiNatrium, lemak $tinggiLemak")

        val tabsAdapter = BatasTabsAdapter(this)
        binding.pagerBatas.adapter = tabsAdapter

        // Hubungkan tab dengan halaman, lengkap dengan ikon dan badge
        TabLayoutMediator(binding.tabBatas, binding.pagerBatas) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.grafik_gula)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_gula)
                    pasangBadge(tab, tinggiGula)
                }
                1 -> {
                    tab.text = getString(R.string.grafik_natrium)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_natrium)
                    pasangBadge(tab, tinggiNatrium)
                }
                2 -> {
                    tab.text = getString(R.string.grafik_lemak)
                    tab.icon = ContextCompat.getDrawable(this, R.drawable.ic_lemak)
                    pasangBadge(tab, tinggiLemak)
                }
            }
        }.attach()

        // Buka tab sesuai tombol yang ditekan di Beranda
        val tabAwal = intent.getIntExtra("tab", 0)
        binding.pagerBatas.setCurrentItem(tabAwal, false)
    }

    // Badge angka hanya muncul jika ada produk berstatus tinggi
    private fun pasangBadge(tab: TabLayout.Tab, jumlah: Int) {
        if (jumlah > 0) {
            val badge = tab.getOrCreateBadge()
            badge.isVisible = true
            badge.number = jumlah
        }
    }

    // Status aturan 5/20, sama seperti warna Grafik Gizi
    fun statusPersen(persen: Int): String {
        if (persen <= 5) {
            return getString(R.string.status_rendah)
        } else if (persen < 20) {
            return getString(R.string.status_sedang)
        } else {
            return getString(R.string.status_tinggi)
        }
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
        Log.e("onDestroy", "BatasHarianActivity dihapus dari stack")
    }
}
