package com.example.adt_nabati.Beranda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

        /** Ganti menjadi versi binding */
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar: berbeda dengan Activity, Fragment harus menggunakan (requireActivity() as AppCompatActivity)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.menu_beranda)
        }

        // Di Fragment, getSharedPreferences dipanggil lewat requireContext()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Tampilkan username yang disimpan saat login
        val username = sharedPref.getString("username", "")
        binding.textSapaan.text = getString(R.string.label_sapaan_user, username)

        // Card produk -> kirim data ke SnackDetailActivity
        binding.cardRicheese.setOnClickListener {
            bukaDetail(getString(R.string.nama_richeese), getString(R.string.desc_richeese),
                R.drawable.nabati_richeese, 37, 190, 10, 2, 24, 11, 70)
        }

        binding.cardRichoco.setOnClickListener {
            bukaDetail(getString(R.string.nama_richoco), getString(R.string.desc_richoco),
                R.drawable.nabati_richoco, 37, 185, 10, 2, 23, 12, 55)
        }

        binding.cardRolls.setOnClickListener {
            bukaDetail(getString(R.string.nama_rolls), getString(R.string.desc_rolls),
                R.drawable.nabati_rolls, 33, 170, 9, 2, 21, 10, 65)
        }
    }

    // Intent + putExtra: kirim data produk ke SnackDetailActivity
    // this -> requireContext() karena sedang berada di Fragment
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
}
