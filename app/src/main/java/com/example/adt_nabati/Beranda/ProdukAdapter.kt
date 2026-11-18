package com.example.adt_nabati.Beranda

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.ItemProdukBinding

// Penghubung data ProdukModel dengan tampilan item_produk.xml
class ProdukAdapter(
    context: Context,
    private val produkList: List<ProdukModel>
) : ArrayAdapter<ProdukModel>(context, 0, produkList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemProdukBinding = ItemProdukBinding.inflate(LayoutInflater.from(context), parent, false)
        val view = binding.root

        val produk = produkList[position]

        // Glide juga bisa memuat gambar dari id drawable
        Glide.with(context)
            .load(produk.gambar)
            .into(binding.imgProduk)

        binding.textNamaProduk.text = produk.nama
        binding.textInfoProduk.text = context.getString(R.string.fmt_info_produk, produk.takaran, produk.energi, produk.gula)

        // Persen gula dari batas 50 g, warna memakai aturan 5/20
        val persenGula = produk.gula * 100 / 50
        binding.textPersenGula.text = context.getString(R.string.fmt_persen_gula, persenGula)
        if (persenGula <= 5) {
            binding.textPersenGula.setTextColor(context.getColor(R.color.hijau_aman))
        } else if (persenGula < 20) {
            binding.textPersenGula.setTextColor(context.getColor(R.color.oranye_waspada))
        } else {
            binding.textPersenGula.setTextColor(context.getColor(R.color.merah_nabati))
        }

        // Klik item membuka detail produk
        view.setOnClickListener {
            Log.e("Katalog", "Item diklik: ${produk.nama}")
            val intent = Intent(context, SnackDetailActivity::class.java)
            intent.putExtra("nama", produk.nama)
            intent.putExtra("deskripsi", produk.deskripsi)
            intent.putExtra("gambar", produk.gambar)
            intent.putExtra("takaran", produk.takaran)
            intent.putExtra("energi", produk.energi)
            intent.putExtra("lemak", produk.lemak)
            intent.putExtra("protein", produk.protein)
            intent.putExtra("karbo", produk.karbo)
            intent.putExtra("gula", produk.gula)
            intent.putExtra("natrium", produk.natrium)
            context.startActivity(intent)
        }

        return view
    }
}
