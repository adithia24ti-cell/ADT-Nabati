package com.example.adt_nabati.Beranda

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.FragmentBatasGulaBinding

class BatasGulaFragment : Fragment() {

    private var _binding: FragmentBatasGulaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "BatasGulaFragment ditampilkan")

        _binding = FragmentBatasGulaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val batasHarian = requireActivity() as BatasHarianActivity

        // Susun persen gula tiap produk terhadap batas 50 g
        var daftar = ""
        for (produk in batasHarian.produkList) {
            val persen = produk.gula * 100 / 50
            daftar += getString(
                R.string.fmt_baris_batas, produk.nama, produk.gula,
                getString(R.string.satuan_gram), persen, batasHarian.statusPersen(persen)
            ) + "\n"
        }
        binding.textDaftarProduk.text = daftar.trim()

        Log.e("Batas Harian", "Tab Gula ditampilkan")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
