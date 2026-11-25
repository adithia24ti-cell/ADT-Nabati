package com.example.adt_nabati.Akun

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adt_nabati.R
import com.example.adt_nabati.data.api.SupabaseApiClient
import com.example.adt_nabati.data.model.FavoritModel
import com.example.adt_nabati.databinding.ActivityFavoritBinding
import com.example.adt_nabati.databinding.DialogCatatanFavoritBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class FavoritActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritBinding

    private var userId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "FavoritActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityFavoritBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_favorit)
            subtitle = getString(R.string.subtitle_riwayat)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        userId = getSharedPreferences("user_pref", MODE_PRIVATE).getLong("userId", 0L)
        binding.rvFavorit.layoutManager = LinearLayoutManager(this)
    }

    // Data dimuat ulang setiap halaman tampil
    override fun onResume() {
        super.onResume()
        muatFavorit()
    }

    // Read: ambil favorit milik user yang login, terbaru di atas
    private fun muatFavorit() {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.getFavorit("eq.$userId", "created_at.desc")
                Log.e("Supabase", "Muat favorit, kode respon: ${response.code()}")
                val daftar = response.body()

                if (response.isSuccessful && daftar != null) {
                    tampilkanDaftar(daftar)
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    tampilkanPesan(getString(R.string.toast_gagal_server, response.code()))
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Muat favorit gagal: ${e.message}")
                tampilkanPesan(getString(R.string.toast_gagal_koneksi))
            }
        }
    }

    private fun tampilkanDaftar(daftar: List<FavoritModel>) {
        binding.rvFavorit.adapter = FavoritAdapter(
            daftar,
            onUbah = { item -> dialogUbahCatatan(item) },
            onHapus = { item -> dialogHapus(item) }
        )
        binding.textRingkasanFavorit.text = getString(R.string.favorit_ringkasan, daftar.size)

        if (daftar.isEmpty()) {
            tampilkanPesan(getString(R.string.favorit_kosong))
        } else {
            binding.textKosongFavorit.visibility = View.GONE
        }
    }

    private fun tampilkanPesan(pesan: String) {
        binding.textKosongFavorit.text = pesan
        binding.textKosongFavorit.visibility = View.VISIBLE
        if (binding.rvFavorit.adapter == null) {
            binding.textRingkasanFavorit.text = getString(R.string.favorit_ringkasan, 0)
        }
    }

    // Update: dialog berisi catatan lama yang bisa diubah
    private fun dialogUbahCatatan(item: FavoritModel) {
        val dialogBinding = DialogCatatanFavoritBinding.inflate(layoutInflater)
        dialogBinding.edtCatatan.setText(item.catatan)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_ubah_catatan_title, item.nama_produk))
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_simpan) { dialog, _ ->
                dialog.dismiss()
                val catatanBaru = dialogBinding.edtCatatan.text.toString().trim()
                ubahCatatan(item, catatanBaru)
            }
            .setNegativeButton(R.string.dialog_batal) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun ubahCatatan(item: FavoritModel, catatanBaru: String) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.ubahFavorit("eq.${item.id}", mapOf("catatan" to catatanBaru))
                Log.e("Supabase", "Ubah catatan favorit id ${item.id}, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@FavoritActivity, R.string.toast_catatan_diubah, Toast.LENGTH_SHORT).show()
                    muatFavorit()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@FavoritActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Ubah catatan gagal: ${e.message}")
                Toast.makeText(this@FavoritActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete: konfirmasi dulu sebelum produk dihapus dari favorit
    private fun dialogHapus(item: FavoritModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_hapus_favorit_title)
            .setMessage(getString(R.string.dialog_hapus_favorit, item.nama_produk))
            .setPositiveButton(R.string.btn_hapus) { dialog, _ ->
                dialog.dismiss()
                hapusFavorit(item)
            }
            .setNegativeButton(R.string.dialog_batal) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun hapusFavorit(item: FavoritModel) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.hapusFavorit("eq.${item.id}")
                Log.e("Supabase", "Hapus favorit id ${item.id}, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@FavoritActivity, R.string.toast_favorit_dihapus, Toast.LENGTH_SHORT).show()
                    muatFavorit()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@FavoritActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Hapus favorit gagal: ${e.message}")
                Toast.makeText(this@FavoritActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "FavoritActivity dihapus dari stack")
    }
}
