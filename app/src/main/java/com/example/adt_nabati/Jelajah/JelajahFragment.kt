package com.example.adt_nabati.Jelajah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.FragmentJelajahBinding

class JelajahFragment : Fragment() {

    private var _binding: FragmentJelajahBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "JelajahFragment ditampilkan")

        _binding = FragmentJelajahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di Fragment, toolbar dipasang lewat requireActivity()
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.menu_jelajah)
        }

        // Buka website Nabati di WebViewActivity
        binding.btnWebView.setOnClickListener {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            startActivity(intent)
        }

        // Daftar tips ngemil, satu teks per baris
        val tipsList = listOf(
            getString(R.string.tip_label),
            getString(R.string.tip_gula),
            getString(R.string.tip_natrium),
            getString(R.string.tip_lemak),
            getString(R.string.tip_wafer),
            getString(R.string.tip_grafik),
            getString(R.string.tip_catat)
        )

        // Adapter penghubung tipsList dengan layout simple_list_item_1
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            tipsList
        )
        binding.listTips.adapter = adapter

        binding.listTips.setOnItemClickListener { _, _, position, _ ->
            val tipsDipilih = tipsList[position]
            Log.e("Tips", "Tips dipilih: $tipsDipilih")
            Toast.makeText(requireContext(), getString(R.string.toast_tips, tipsDipilih), Toast.LENGTH_SHORT).show()
        }
    }
}
