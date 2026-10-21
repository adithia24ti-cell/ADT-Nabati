package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.R
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

        // Ambil data Intent
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            subtitle = nama
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // Tampilkan data ke layout
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

        // AlertDialog: konfirmasi sebelum mencatat
        binding.btnCatat.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(getString(R.string.dialog_message, nama, gula))
                .setPositiveButton(R.string.dialog_ya) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Ya, $nama dicatat")
                    Snackbar.make(binding.root, getString(R.string.snackbar_tercatat, nama), Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_undo) {
                            Log.e("Info Snackbar", "Pencatatan $nama dibatalkan")
                        }
                        .show()
                }
                .setNegativeButton(R.string.dialog_batal) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Batal")
                }
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_favorit -> {
                Toast.makeText(this, R.string.toast_favorit, Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_info -> {
                Toast.makeText(this, R.string.toast_info, Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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