package com.example.adt_nabati.Jelajah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.databinding.FragmentJelajahBinding

class JelajahFragment : Fragment() {

    private var _binding: FragmentJelajahBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "JelajahFragment ditampilkan")

        /** Ganti menjadi versi binding */
        _binding = FragmentJelajahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ke WebViewActivity (website Nabati), this -> requireContext()
        binding.btnWebView.setOnClickListener {
            val intent = Intent(requireContext(), WebViewActivity::class.java)
            startActivity(intent)
        }
    }
}
