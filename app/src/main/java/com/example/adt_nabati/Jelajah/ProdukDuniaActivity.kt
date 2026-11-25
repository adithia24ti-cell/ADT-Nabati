package com.example.adt_nabati.Jelajah

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adt_nabati.R
import com.example.adt_nabati.data.api.OpenFoodFactsApiClient
import com.example.adt_nabati.data.model.ProdukDuniaModel
import com.example.adt_nabati.databinding.ActivityProdukDuniaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProdukDuniaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdukDuniaBinding

    // Produk yang lolos saring, dipakai ulang saat mode tampilan diganti
    private var daftarProduk: List<ProdukDuniaModel> = listOf()

    // Mode tampilan RecyclerView: grid, list, atau horizontal
    private var mode = "grid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "ProdukDuniaActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityProdukDuniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_produk_dunia)
            subtitle = getString(R.string.subtitle_produk_dunia)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // Seperti tombol Refresh catfact, fakta diganti tanpa memanggil API lagi
        binding.btnFaktaLain.setOnClickListener {
            tampilkanFaktaGula()
        }

        loadProduk()
    }

    // Ambil produk bermerek Nabati dari Open Food Facts
    private fun loadProduk() {
        binding.textJumlahProduk.text = getString(R.string.dunia_memuat)
        lifecycleScope.launch {
            try {
                val response = OpenFoodFactsApiClient.apiService.cariProduk(
                    "nabati",
                    "code,product_name,brands,image_front_small_url,nutriscore_grade,nutriments",
                    50
                )
                Log.e("Produk Dunia", "Muat produk, kode respon: ${response.code()}")
                val hasil = response.body()

                if (response.isSuccessful && hasil != null) {
                    // Data komunitas: buang produk tanpa nama atau tanpa foto
                    val tersaring = ArrayList<ProdukDuniaModel>()
                    for (produk in hasil.products) {
                        if (!produk.product_name.isNullOrBlank() && !produk.image_front_small_url.isNullOrBlank()) {
                            tersaring.add(produk)
                        }
                    }
                    daftarProduk = tersaring
                    Log.e("Produk Dunia", "${hasil.products.size} produk diterima, ${tersaring.size} ditampilkan")

                    binding.textJumlahProduk.text = getString(R.string.dunia_jumlah, tersaring.size)
                    pasangAdapter()
                    tampilkanFaktaGula()
                } else {
                    binding.textJumlahProduk.text = getString(R.string.toast_gagal_server, response.code())
                }
            } catch (e: Exception) {
                Log.e("Produk Dunia", "Gagal memuat: ${e.message}")
                binding.textJumlahProduk.text = getString(R.string.toast_gagal_koneksi)
                binding.tvFaktaGula.text = getString(R.string.fakta_gagal)
                Toast.makeText(this@ProdukDuniaActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tiga jenis layoutManager seperti bagian Mode RecyclerView di modul
    private fun pasangAdapter() {
        if (mode == "list") {
            binding.rvProdukDunia.layoutManager = LinearLayoutManager(this)
        } else if (mode == "horizontal") {
            binding.rvProdukDunia.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.rvProdukDunia.layoutManager = GridLayoutManager(this, 2)
        }
        binding.rvProdukDunia.adapter = ProdukDuniaAdapter(daftarProduk, mode == "horizontal") { produk ->
            dialogDetailProduk(produk)
        }
        Log.e("Produk Dunia", "Mode tampilan: $mode")
    }

    // Pilih satu produk acak yang punya data gula
    private fun tampilkanFaktaGula() {
        val punyaGula = ArrayList<ProdukDuniaModel>()
        for (produk in daftarProduk) {
            if (produk.nutriments?.sugars_100g != null) {
                punyaGula.add(produk)
            }
        }
        if (punyaGula.isEmpty()) {
            binding.tvFaktaGula.text = getString(R.string.fakta_memuat)
            return
        }

        val produk = punyaGula.random()
        val gula = produk.nutriments?.sugars_100g ?: 0.0
        val persen = (gula * 100 / 50).toInt()
        binding.tvFaktaGula.text = getString(R.string.fakta_gula, produk.product_name, gula, persen)
    }

    // Detail singkat saat kartu produk diklik
    private fun dialogDetailProduk(produk: ProdukDuniaModel) {
        val gizi = produk.nutriments
        val tidakAda = getString(R.string.dunia_tidak_ada)
        val gula = if (gizi?.sugars_100g != null) getString(R.string.fmt_satu_desimal, gizi.sugars_100g) else tidakAda
        val lemak = if (gizi?.fat_100g != null) getString(R.string.fmt_satu_desimal, gizi.fat_100g) else tidakAda
        val garam = if (gizi?.salt_100g != null) getString(R.string.fmt_satu_desimal, gizi.salt_100g) else tidakAda
        val skor = (produk.nutriscore_grade ?: "-").uppercase()

        MaterialAlertDialogBuilder(this)
            .setTitle(produk.product_name)
            .setMessage(getString(R.string.dunia_detail, produk.brands ?: "-", gula, lemak, garam, skor, produk.code ?: "-"))
            .setPositiveButton(R.string.dialog_ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.produk_dunia_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_muat_ulang -> {
                loadProduk()
                true
            }
            R.id.action_mode_grid -> {
                mode = "grid"
                pasangAdapter()
                true
            }
            R.id.action_mode_list -> {
                mode = "list"
                pasangAdapter()
                true
            }
            R.id.action_mode_horizontal -> {
                mode = "horizontal"
                pasangAdapter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "ProdukDuniaActivity dihapus dari stack")
    }
}
