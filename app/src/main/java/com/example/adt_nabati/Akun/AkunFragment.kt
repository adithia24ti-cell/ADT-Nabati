package com.example.adt_nabati.Akun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.adt_nabati.AuthActivity
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.FragmentAkunBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("onCreateView", "AkunFragment ditampilkan")

        /** Ganti menjadi versi binding */
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di Fragment, getSharedPreferences dipanggil lewat requireContext()
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Tampilkan username yang disimpan saat login
        val username = sharedPref.getString("username", "")
        binding.textAkunDesc.text = getString(R.string.akun_desc, username)

        // Ke halaman Profil Developer
        binding.btnProfil.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        // Logout: konfirmasi AlertDialog -> hapus sharedPreferences -> ke AuthActivity
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout_title)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout_ya) { dialog, _ ->
                    val editor = sharedPref.edit()
                    editor.clear()
                    editor.apply()
                    Log.e("Info Logout", "sharedPreferences dihapus, isLogin kembali false")

                    dialog.dismiss()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    // Destroy BaseActivity lewat requireActivity() karena sedang di Fragment
                    requireActivity().finish()
                }
                .setNegativeButton(R.string.dialog_batal) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
