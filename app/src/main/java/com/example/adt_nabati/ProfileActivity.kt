package com.example.adt_nabati

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adt_nabati.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate", "ProfileActivity dibuat pertama kali")

        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar dengan tombol kembali
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.title_profil)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }

        // Sosial media dibuka di WebViewActivity (Intent + putExtra)
        binding.valInstagram.setOnClickListener {
            bukaWeb(getString(R.string.url_instagram), getString(R.string.label_instagram))
        }
        binding.valGithub.setOnClickListener {
            bukaWeb(getString(R.string.url_github), getString(R.string.label_github))
        }
        binding.valLinkedin.setOnClickListener {
            bukaWeb(getString(R.string.url_linkedin), getString(R.string.label_linkedin))
        }
    }

    private fun bukaWeb(url: String, judul: String) {
        Log.e("Info Profil", "Buka $judul: $url")
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("judul", judul)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy", "ProfileActivity dihapus dari stack")
    }
}