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
import com.example.adt_nabati.data.model.KonsumsiModel
import com.example.adt_nabati.databinding.ActivityRiwayatKonsumsiBinding
import com.example.adt_nabati.databinding.DialogUbahJumlahBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class RiwayatKonsumsiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRiwayatKonsumsiBinding

    private var userId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "RiwayatKonsumsiActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityRiwayatKonsumsiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_riwayat)
            subtitle = getString(R.string.subtitle_riwayat)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        userId = getSharedPreferences("user_pref", MODE_PRIVATE).getLong("userId", 0L)
        binding.rvKonsumsi.layoutManager = LinearLayoutManager(this)
    }

    // Data dimuat ulang setiap halaman tampil, termasuk setelah kembali dari Detail
    override fun onResume() {
        super.onResume()
        muatKonsumsi()
    }

    // Read: ambil riwayat milik user yang login, terbaru di atas
    private fun muatKonsumsi() {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.getKonsumsi("eq.$userId", "created_at.desc")
                Log.e("Supabase", "Muat riwayat konsumsi, kode respon: ${response.code()}")
                val daftar = response.body()

                if (response.isSuccessful && daftar != null) {
                    tampilkanDaftar(daftar)
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    tampilkanPesan(getString(R.string.toast_gagal_server, response.code()))
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Muat riwayat gagal: ${e.message}")
                tampilkanPesan(getString(R.string.toast_gagal_koneksi))
            }
        }
    }

    private fun tampilkanDaftar(daftar: List<KonsumsiModel>) {
        binding.rvKonsumsi.adapter = KonsumsiAdapter(
            daftar,
            onUbah = { item -> dialogUbahJumlah(item) },
            onHapus = { item -> dialogHapus(item) }
        )

        // Ringkasan total gula dari seluruh catatan
        var totalGula = 0
        for (item in daftar) {
            totalGula += item.gula * item.jumlah
        }
        binding.textRingkasan.text = getString(R.string.riwayat_ringkasan, daftar.size, totalGula)

        if (daftar.isEmpty()) {
            tampilkanPesan(getString(R.string.riwayat_kosong))
        } else {
            binding.textKosong.visibility = View.GONE
        }
    }

    private fun tampilkanPesan(pesan: String) {
        binding.textKosong.text = pesan
        binding.textKosong.visibility = View.VISIBLE
        if (binding.rvKonsumsi.adapter == null) {
            binding.textRingkasan.text = getString(R.string.riwayat_ringkasan, 0, 0)
        }
    }

    // Update: dialog berisi TextInputLayout untuk jumlah bungkus baru
    private fun dialogUbahJumlah(item: KonsumsiModel) {
        val dialogBinding = DialogUbahJumlahBinding.inflate(layoutInflater)
        dialogBinding.edtJumlah.setText(item.jumlah.toString())

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_ubah_title, item.nama_produk))
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_simpan, null)
            .setNegativeButton(R.string.dialog_batal) { d, _ -> d.dismiss() }
            .show()

        // Tombol Simpan dipasang manual agar dialog tidak tertutup saat isian salah
        dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            val jumlahBaru = dialogBinding.edtJumlah.text.toString().toIntOrNull()
            if (jumlahBaru == null || jumlahBaru < 1 || jumlahBaru > 20) {
                dialogBinding.tilJumlah.error = getString(R.string.error_jumlah)
            } else {
                dialog.dismiss()
                ubahJumlah(item, jumlahBaru)
            }
        }
    }

    private fun ubahJumlah(item: KonsumsiModel, jumlahBaru: Int) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.ubahKonsumsi("eq.${item.id}", mapOf("jumlah" to jumlahBaru))
                Log.e("Supabase", "Ubah jumlah id ${item.id} jadi $jumlahBaru, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@RiwayatKonsumsiActivity, R.string.toast_jumlah_diubah, Toast.LENGTH_SHORT).show()
                    muatKonsumsi()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@RiwayatKonsumsiActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Ubah jumlah gagal: ${e.message}")
                Toast.makeText(this@RiwayatKonsumsiActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete: konfirmasi dulu sebelum baris dihapus dari tabel
    private fun dialogHapus(item: KonsumsiModel) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_hapus_title)
            .setMessage(getString(R.string.dialog_hapus_konsumsi, item.nama_produk))
            .setPositiveButton(R.string.btn_hapus) { dialog, _ ->
                dialog.dismiss()
                hapusKonsumsi(item)
            }
            .setNegativeButton(R.string.dialog_batal) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun hapusKonsumsi(item: KonsumsiModel) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.hapusKonsumsi("eq.${item.id}")
                Log.e("Supabase", "Hapus konsumsi id ${item.id}, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@RiwayatKonsumsiActivity, R.string.toast_konsumsi_dihapus, Toast.LENGTH_SHORT).show()
                    muatKonsumsi()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@RiwayatKonsumsiActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Hapus konsumsi gagal: ${e.message}")
                Toast.makeText(this@RiwayatKonsumsiActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
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
        Log.e("onDestroy", "RiwayatKonsumsiActivity dihapus dari stack")
    }
}
