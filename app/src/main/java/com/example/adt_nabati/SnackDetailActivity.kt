package com.example.adt_nabati

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivitySnackDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SnackDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySnackDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "SnackDetailActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivitySnackDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nama = intent.getStringExtra("nama")
        val deskripsi = intent.getStringExtra("deskripsi")
        val gambar = intent.getIntExtra("gambar", R.drawable.nabati_richeese)
        val takaran = intent.getIntExtra("takaran", 0)
        val energi = intent.getIntExtra("energi", 0)
        val lemak = intent.getIntExtra("lemak", 0)
        val protein = intent.getIntExtra("protein", 0)
        val karbo = intent.getIntExtra("karbo", 0)
        val gula = intent.getIntExtra("gula", 0)
        val natrium = intent.getIntExtra("natrium", 0)

        Log.e("Data Intent", "Produk: $nama, Energi: $energi kkal, Gula: $gula g")
        binding.imgSnack.setImageResource(gambar)
        binding.textNamaSnack.text = nama
        binding.textDeskripsi.text = deskripsi
        binding.valTakaran.text = getString(R.string.fmt_gram, takaran)
        binding.valEnergi.text = getString(R.string.fmt_kkal, energi)
        binding.valLemak.text = getString(R.string.fmt_gram, lemak)
        binding.valProtein.text = getString(R.string.fmt_gram, protein)
        binding.valKarbo.text = getString(R.string.fmt_gram, karbo)
        binding.valGula.text = getString(R.string.fmt_gram, gula)
        binding.valNatrium.text = getString(R.string.fmt_mg, natrium)

        binding.btnCatat.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(getString(R.string.dialog_message, nama, gula))
                .setPositiveButton(R.string.dialog_ya) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Ya, $nama dicatat")
                    tampilkanSnackbarTercatat(nama)
                }
                .setNegativeButton(R.string.dialog_batal) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Batal")
                }
                .show()
        }

        binding.btnBack.setOnClickListener { kembali() }
        binding.btnKembali.setOnClickListener { kembali() }
    }

    private fun kembali() {
        Log.e("Navigasi", "Kembali dari SnackDetailActivity ke MainActivity")
        finish()
    }

    private fun tampilkanSnackbarTercatat(nama: String?) {
        Snackbar.make(binding.root, getString(R.string.snackbar_tercatat, nama), Snackbar.LENGTH_LONG)
            .setAction(R.string.snackbar_undo) {
                Log.e("Info Snackbar", "Pencatatan $nama dibatalkan")
                Snackbar.make(binding.root, R.string.snackbar_dibatalkan, Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    override fun onStart() {
        super.onStart()
        Log.e("onStart", "onStart: SnackDetailActivity terlihat di layar")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "SnackDetailActivity dihapus dari stack")
    }
}