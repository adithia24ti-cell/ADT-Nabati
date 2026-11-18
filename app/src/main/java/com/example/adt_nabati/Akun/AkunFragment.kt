package com.example.adt_nabati.Akun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Di Fragment, toolbar dipasang lewat requireActivity()
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            title = getString(R.string.menu_akun)
        }

        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)

        // Tampilkan username yang disimpan saat login
        val username = sharedPref.getString("username", "")
        binding.textAkunDesc.text = getString(R.string.akun_desc, username)

        // Data menu berupa pasangan judul dan keterangan untuk simple_list_item_2
        val menuAkun = listOf(
            mapOf("judul" to getString(R.string.menu_target), "keterangan" to getString(R.string.desc_target)),
            mapOf("judul" to getString(R.string.menu_sumber), "keterangan" to getString(R.string.desc_sumber)),
            mapOf("judul" to getString(R.string.menu_tentang), "keterangan" to getString(R.string.desc_tentang)),
            mapOf("judul" to getString(R.string.menu_catatan), "keterangan" to getString(R.string.desc_catatan))
        )

        val adapter = SimpleAdapter(
            requireContext(),
            menuAkun,
            android.R.layout.simple_list_item_2,
            arrayOf("judul", "keterangan"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )
        binding.listMenuAkun.adapter = adapter

        binding.listMenuAkun.setOnItemClickListener { _, _, position, _ ->
            val menuDipilih = menuAkun[position]
            val judul = menuDipilih["judul"]
            val keterangan = menuDipilih["keterangan"]
            Log.e("Menu Akun", "Menu dipilih: $judul")
            Toast.makeText(requireContext(), getString(R.string.toast_menu_akun, judul, keterangan), Toast.LENGTH_SHORT).show()
        }

        binding.btnProfil.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        // Konfirmasi logout, lalu hapus data login dan kembali ke halaman login
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
                    // Tutup BaseActivity agar tidak bisa kembali setelah logout
                    requireActivity().finish()
                }
                .setNegativeButton(R.string.dialog_batal) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}
