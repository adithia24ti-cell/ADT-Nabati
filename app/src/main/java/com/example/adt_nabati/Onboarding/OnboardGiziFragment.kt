package com.example.adt_nabati.Onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.databinding.FragmentOnboardGiziBinding

// Halaman panduan: kenali gizi tiap camilan
class OnboardGiziFragment : Fragment() {

    private var _binding: FragmentOnboardGiziBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("Onboarding", "OnboardGiziFragment ditampilkan")

        _binding = FragmentOnboardGiziBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
