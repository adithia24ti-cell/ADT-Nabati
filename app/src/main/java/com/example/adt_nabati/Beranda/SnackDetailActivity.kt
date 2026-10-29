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

        // FITUR BARU 1: tampilkan grafik gizi dari data Intent
        tampilkanPieGizi(lemak, protein, karbo, energi)
        tampilkanBarBatas(gula, natrium, lemak)

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

    // FITUR BARU 1: PieChart komposisi lemak, protein, karbohidrat per sajian
    private fun tampilkanPieGizi(lemak: Int, protein: Int, karbo: Int, energi: Int) {
        val pieChart = binding.pieGizi
        pieChart.setNoDataText(getString(R.string.grafik_kosong))

        // Jika semua data 0, grafik tidak digambar (persen tidak bisa dihitung)
        if (lemak + protein + karbo == 0) {
            Log.e("Grafik Gizi", "Data pie kosong, grafik tidak ditampilkan")
            return
        }

        // Data potongan pie (gram per sajian)
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(lemak.toFloat(), getString(R.string.grafik_lemak)))
        entries.add(PieEntry(protein.toFloat(), getString(R.string.grafik_protein)))
        entries.add(PieEntry(karbo.toFloat(), getString(R.string.grafik_karbo)))

        // Warna tiap potongan dari palet ADT-Nabati
        val colors: ArrayList<Int> = ArrayList()
        colors.add(getColor(R.color.merah_nabati))
        colors.add(getColor(R.color.abu_gelap))
        colors.add(getColor(R.color.oranye_waspada))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Nilai ditampilkan dalam persen
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

    // FITUR BARU 1: BarChart persen batas harian Kemenkes per bungkus
    private fun tampilkanBarBatas(gula: Int, natrium: Int, lemak: Int) {
        val barChart = binding.barBatas
        barChart.setNoDataText(getString(R.string.grafik_kosong))

        // Hitung persen dari batas harian (gula 50 g, natrium 2.000 mg, lemak 67 g)
        val persenGula = gula * 100 / 50
        val persenNatrium = natrium * 100 / 2000
        val persenLemak = lemak * 100 / 67

        // Data batang: indeks 0 = Gula, 1 = Natrium, 2 = Lemak
        val entries: ArrayList<BarEntry> = ArrayList()
        entries.add(BarEntry(0f, persenGula.toFloat()))
        entries.add(BarEntry(1f, persenNatrium.toFloat()))
        entries.add(BarEntry(2f, persenLemak.toFloat()))

        // Warna batang sesuai status persen
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

        // Label sumbu X dari strings.xml
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

        // Kalimat ringkasan di bawah grafik
        binding.textRingkasanGrafik.text =
            getString(R.string.fmt_ringkasan_grafik, persenGula, persenNatrium, persenLemak)

        Log.e("Grafik Gizi", "Bar: gula $persenGula%, natrium $persenNatrium%, lemak $persenLemak% dari batas harian")
    }

    // FITUR BARU 1: warna status aturan 5/20 (<= 5% rendah, 6–19% sedang, >= 20% tinggi)
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