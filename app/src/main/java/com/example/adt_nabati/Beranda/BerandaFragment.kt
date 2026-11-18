package com.example.adt_nabati.Beranda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.adt_nabati.Onboarding.OnboardingActivity
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.FragmentBerandaBinding

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "BerandaFragment ditampilkan")

        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di Fragment, toolbar dipasang lewat requireActivity()
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.menu_beranda)
        }

        // Tampilkan ikon Panduan di Toolbar
        setHasOptionsMenu(true)

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Tampilkan username yang disimpan saat login
        val username = sharedPref.getString("username", "")
        binding.textSapaan.text = getString(R.string.label_sapaan_user, username)

        // Data produk untuk grid, nilainya sama dengan label kemasan
        val produkList = listOf(
            ProdukModel(getString(R.string.nama_richeese), getString(R.string.desc_richeese),
                R.drawable.nabati_richeese, getString(R.string.chip_keju), 37, 190, 10, 2, 24, 11, 70),
            ProdukModel(getString(R.string.nama_richoco), getString(R.string.desc_richoco),
                R.drawable.nabati_richoco, getString(R.string.chip_cokelat), 37, 185, 10, 2, 23, 12, 55),
            ProdukModel(getString(R.string.nama_rolls), getString(R.string.desc_rolls),
                R.drawable.nabati_rolls, getString(R.string.chip_keju), 33, 170, 9, 2, 21, 10, 65)
        )

        // Klik kotak produk membuka detail beserta data gizinya
        val adapter = ProdukGridAdapter(produkList) { produkDipilih ->
            Log.e("Grid Produk", "Produk dipilih: ${produkDipilih.nama}")
            bukaDetail(produkDipilih.nama, produkDipilih.deskripsi, produkDipilih.gambar,
                produkDipilih.takaran, produkDipilih.energi, produkDipilih.lemak, produkDipilih.protein,
                produkDipilih.karbo, produkDipilih.gula, produkDipilih.natrium)
        }

        binding.rvProduk.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            this.adapter = adapter
        }

        // Tombol di grid membuka halaman Batas Harian pada tab yang sesuai
        binding.btnBatasGula.setOnClickListener {
            bukaBatasHarian(0)
        }

        binding.btnBatasNatrium.setOnClickListener {
            bukaBatasHarian(1)
        }

        binding.btnBatasLemak.setOnClickListener {
            bukaBatasHarian(2)
        }

        // Buka daftar semua produk di KatalogActivity
        binding.btnKatalog.setOnClickListener {
            val intent = Intent(requireContext(), KatalogActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.beranda_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_panduan -> {
                Log.e("Onboarding", "Panduan dibuka dari Beranda")
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Buka BatasHarianActivity dengan tab awal tertentu
    private fun bukaBatasHarian(tab: Int) {
        Log.e("Batas Harian", "Buka tab ke-$tab")
        val intent = Intent(requireContext(), BatasHarianActivity::class.java)
        intent.putExtra("tab", tab)
        startActivity(intent)
    }

    // Kirim data produk ke SnackDetailActivity
    private fun bukaDetail(
        nama: String, deskripsi: String, gambar: Int,
        takaran: Int, energi: Int, lemak: Int, protein: Int,
        karbo: Int, gula: Int, natrium: Int
    ) {
        val intent = Intent(requireContext(), SnackDetailActivity::class.java)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
