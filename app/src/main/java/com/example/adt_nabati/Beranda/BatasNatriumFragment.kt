package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.FragmentBatasNatriumBinding

class BatasNatriumFragment : Fragment() {

    private var _binding: FragmentBatasNatriumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "BatasNatriumFragment ditampilkan")

        _binding = FragmentBatasNatriumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val batasHarian = requireActivity() as BatasHarianActivity

        // Susun persen natrium tiap produk terhadap batas 2000 mg
        var daftar = ""
        for (produk in batasHarian.produkList) {
            val persen = produk.natrium * 100 / 2000
            daftar += getString(
                R.string.fmt_baris_batas, produk.nama, produk.natrium,
                getString(R.string.satuan_miligram), persen, batasHarian.statusPersen(persen)
            ) + "\n"
        }
        binding.textDaftarProduk.text = daftar.trim()

        Log.e("Batas Harian", "Tab Natrium ditampilkan")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
