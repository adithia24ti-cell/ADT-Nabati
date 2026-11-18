package com.example.adt_nabati.Onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.databinding.FragmentOnboardBatasBinding

// Halaman panduan: pantau batas harian Kemenkes
class OnboardBatasFragment : Fragment() {

    private var _binding: FragmentOnboardBatasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("Onboarding", "OnboardBatasFragment ditampilkan")

        _binding = FragmentOnboardBatasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
