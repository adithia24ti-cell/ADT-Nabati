package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.adt_nabati.R
import com.example.adt_nabati.data.api.SupabaseApiClient
import com.example.adt_nabati.data.model.FavoritModel
import com.example.adt_nabati.data.model.KonsumsiModel
import com.example.adt_nabati.databinding.ActivitySnackDetailBinding
import com.example.adt_nabati.databinding.DialogCatatanFavoritBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.Locale

class SnackDetailActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivitySnackDetailBinding

    private lateinit var tts: TextToSpeech

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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_detail)
            subtitle = nama
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

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

        tampilkanPieGizi(lemak, protein, karbo, energi)
        tampilkanBarBatas(gula, natrium, lemak)

        // Siapkan Text-to-Speech, hasilnya diterima di onInit
        tts = TextToSpeech(this, this)

        // Kalimat info gizi yang dibacakan
        val teksInfoGizi = getString(
            R.string.tts_info_gizi, nama, takaran, energi, gula, natrium, lemak, gula * 100 / 50
        )

        // Klik untuk mulai bicara, klik lagi untuk berhenti
        binding.btnDengarkan.setOnClickListener {
            if (tts.isSpeaking) {
                tts.stop()
                Toast.makeText(this, R.string.toast_tts_berhenti, Toast.LENGTH_SHORT).show()
                Log.e("TTS", "Suara dihentikan oleh pengguna")
            } else {
                tts.speak(teksInfoGizi, TextToSpeech.QUEUE_FLUSH, null, "info_gizi")
                Log.e("TTS", "Mulai membacakan info gizi $nama")
            }
        }

        // Konfirmasi sebelum mencatat konsumsi
        binding.btnCatat.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_title)
                .setMessage(getString(R.string.dialog_message, nama, gula))
                .setPositiveButton(R.string.dialog_ya) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Ya, $nama dicatat")
                    simpanKonsumsi(nama ?: "", gula, natrium, lemak)
                }
                .setNegativeButton(R.string.dialog_batal) { dialog, _ ->
                    dialog.dismiss()
                    Log.e("Info Dialog", "Pengguna memilih Batal")
                }
                .show()
        }
    }

    // Simpan 1 bungkus ke tabel konsumsi, jumlah bisa diubah nanti di halaman Riwayat
    private fun simpanKonsumsi(nama: String, gula: Int, natrium: Int, lemak: Int) {
        val userId = getSharedPreferences("user_pref", MODE_PRIVATE).getLong("userId", 0L)
        val konsumsi = KonsumsiModel(
            user_id = userId, nama_produk = nama, jumlah = 1,
            gula = gula, natrium = natrium, lemak = lemak
        )

        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.tambahKonsumsi(konsumsi)
                Log.e("Supabase", "Catat konsumsi $nama, kode respon: ${response.code()}")
                val hasil = response.body()

                if (response.isSuccessful && hasil != null && hasil.isNotEmpty()) {
                    val idBaru = hasil[0].id
                    Snackbar.make(binding.root, getString(R.string.snackbar_tercatat, nama), Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_undo) {
                            Log.e("Info Snackbar", "Pencatatan $nama dibatalkan")
                            batalkanKonsumsi(idBaru)
                        }
                        .show()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@SnackDetailActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Catat konsumsi gagal: ${e.message}")
                Toast.makeText(this@SnackDetailActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tombol Batalkan di Snackbar menghapus baris yang baru saja dibuat
    private fun batalkanKonsumsi(id: Long?) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.hapusKonsumsi("eq.$id")
                Log.e("Supabase", "Batalkan konsumsi id $id, kode respon: ${response.code()}")
                if (response.isSuccessful) {
                    Toast.makeText(this@SnackDetailActivity, R.string.toast_catat_dibatalkan, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Batalkan konsumsi gagal: ${e.message}")
                Toast.makeText(this@SnackDetailActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Produk yang sudah ada di favorit tidak ditambahkan lagi
    private fun cekLaluTambahFavorit(nama: String) {
        val userId = getSharedPreferences("user_pref", MODE_PRIVATE).getLong("userId", 0L)

        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.cekFavorit("eq.$userId", "eq.$nama")
                val hasil = response.body()
                Log.e("Supabase", "Cek favorit $nama, kode respon: ${response.code()}")

                if (!response.isSuccessful || hasil == null) {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@SnackDetailActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                } else if (hasil.isNotEmpty()) {
                    Toast.makeText(this@SnackDetailActivity, R.string.toast_favorit_sudah_ada, Toast.LENGTH_SHORT).show()
                } else {
                    dialogTambahFavorit(userId, nama)
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Cek favorit gagal: ${e.message}")
                Toast.makeText(this@SnackDetailActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dialog catatan opsional sebelum produk disimpan ke tabel favorit
    private fun dialogTambahFavorit(userId: Long, nama: String) {
        val dialogBinding = DialogCatatanFavoritBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.dialog_tambah_favorit_title, nama))
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_simpan) { dialog, _ ->
                dialog.dismiss()
                val catatan = dialogBinding.edtCatatan.text.toString().trim()
                simpanFavorit(FavoritModel(user_id = userId, nama_produk = nama, catatan = catatan))
            }
            .setNegativeButton(R.string.dialog_batal) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun simpanFavorit(favorit: FavoritModel) {
        lifecycleScope.launch {
            try {
                val response = SupabaseApiClient.apiService.tambahFavorit(favorit)
                Log.e("Supabase", "Tambah favorit ${favorit.nama_produk}, kode respon: ${response.code()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@SnackDetailActivity, R.string.toast_favorit, Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Supabase", "Pesan server: ${response.errorBody()?.string()}")
                    Toast.makeText(this@SnackDetailActivity, getString(R.string.toast_gagal_server, response.code()), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Supabase", "Tambah favorit gagal: ${e.message}")
                Toast.makeText(this@SnackDetailActivity, R.string.toast_gagal_koneksi, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dipanggil saat mesin TTS selesai disiapkan
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hasil = tts.setLanguage(Locale("id", "ID"))
            if (hasil == TextToSpeech.LANG_MISSING_DATA || hasil == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, R.string.toast_tts_gagal, Toast.LENGTH_LONG).show()
                Log.e("TTS", "Bahasa Indonesia tidak tersedia, kode: $hasil")
            } else {
                binding.btnDengarkan.isEnabled = true
                Log.e("TTS", "TTS siap, bahasa Indonesia aktif")
            }
        } else {
            Toast.makeText(this, R.string.toast_tts_gagal, Toast.LENGTH_LONG).show()
            Log.e("TTS", "Inisialisasi TTS gagal, status: $status")
        }
    }

    // PieChart komposisi lemak, protein, dan karbohidrat per sajian
    private fun tampilkanPieGizi(lemak: Int, protein: Int, karbo: Int, energi: Int) {
        val pieChart = binding.pieGizi
        pieChart.setNoDataText(getString(R.string.grafik_kosong))

        if (lemak + protein + karbo == 0) {
            Log.e("Grafik Gizi", "Data pie kosong, grafik tidak ditampilkan")
            return
        }

        // Data potongan pie dalam gram per sajian
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(lemak.toFloat(), getString(R.string.grafik_lemak)))
        entries.add(PieEntry(protein.toFloat(), getString(R.string.grafik_protein)))
        entries.add(PieEntry(karbo.toFloat(), getString(R.string.grafik_karbo)))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(getColor(R.color.merah_nabati))
        colors.add(getColor(R.color.abu_gelap))
        colors.add(getColor(R.color.oranye_waspada))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(12f)
        data.setValueTextColor(getColor(R.color.white))

        // Pengaturan tampilan pie
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(getColor(R.color.white))
        pieChart.holeRadius = 52f
        pieChart.transparentCircleRadius = 57f
        pieChart.centerText = getString(R.string.fmt_kkal, energi)
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(getColor(R.color.tinta))
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = true
        pieChart.legend.textColor = getColor(R.color.tinta)

        pieChart.data = data
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.invalidate()

        Log.e("Grafik Gizi", "Pie: lemak $lemak g, protein $protein g, karbo $karbo g, energi $energi kkal")
    }

    // BarChart persen batas harian Kemenkes per bungkus
    private fun tampilkanBarBatas(gula: Int, natrium: Int, lemak: Int) {
        val barChart = binding.barBatas
        barChart.setNoDataText(getString(R.string.grafik_kosong))

        // Hitung persen dari batas harian
        val persenGula = gula * 100 / 50
        val persenNatrium = natrium * 100 / 2000
        val persenLemak = lemak * 100 / 67

        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0f, persenGula.toFloat()))
        entries.add(BarEntry(1f, persenNatrium.toFloat()))
        entries.add(BarEntry(2f, persenLemak.toFloat()))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(warnaStatus(persenGula))
        colors.add(warnaStatus(persenNatrium))
        colors.add(warnaStatus(persenLemak))

        val dataSet = BarDataSet(entries, getString(R.string.sub_bar))
        dataSet.colors = colors
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = getColor(R.color.tinta)
        dataSet.valueFormatter = PercentFormatter()

        val data = BarData(dataSet)
        data.barWidth = 0.5f

        val labels = arrayOf(
            getString(R.string.grafik_gula),
            getString(R.string.grafik_natrium),
            getString(R.string.grafik_lemak)
        )
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.textColor = getColor(R.color.tinta)

        // Pengaturan sumbu Y, legend, dan deskripsi
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisLeft.textColor = getColor(R.color.abu_gelap)
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.setFitBars(true)

        barChart.data = data
        barChart.animateY(1400)
        barChart.invalidate()

        binding.textRingkasanGrafik.text =
            getString(R.string.fmt_ringkasan_grafik, persenGula, persenNatrium, persenLemak)

        Log.e("Grafik Gizi", "Bar: gula $persenGula%, natrium $persenNatrium%, lemak $persenLemak% dari batas harian")
    }

    // Warna status aturan 5/20: sampai 5% rendah, di bawah 20% sedang, selebihnya tinggi
    private fun warnaStatus(persen: Int): Int {
        if (persen <= 5) {
            return getColor(R.color.hijau_aman)
        } else if (persen < 20) {
            return getColor(R.color.oranye_waspada)
        } else {
            return getColor(R.color.merah_nabati)
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
                cekLaluTambahFavorit(intent.getStringExtra("nama") ?: "")
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

    // Hentikan suara saat halaman tidak terlihat
    override fun onStop() {
        super.onStop()
        tts.stop()
        Log.e("TTS", "onStop: suara dihentikan")
    }

    override fun onDestroy() {
        // Hentikan suara dan lepaskan mesin TTS
        tts.stop()
        tts.shutdown()
        Log.e("TTS", "onDestroy: TTS di-shutdown")
        super.onDestroy()
        Log.e("onDestroy", "SnackDetailActivity dihapus dari stack")
    }
}