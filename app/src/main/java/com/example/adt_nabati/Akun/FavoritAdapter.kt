package com.example.adt_nabati.Akun

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adt_nabati.R
import com.example.adt_nabati.data.model.FavoritModel
import com.example.adt_nabati.databinding.ItemFavoritBinding

// Adapter daftar produk favorit, aksi ubah catatan dan hapus dikirim ke Activity lewat lambda
class FavoritAdapter(
    private val items: List<FavoritModel>,
    private val onUbah: (FavoritModel) -> Unit,
    private val onHapus: (FavoritModel) -> Unit
) : RecyclerView.Adapter<FavoritAdapter.FavoritViewHolder>() {

    inner class FavoritViewHolder(val binding: ItemFavoritBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritViewHolder {
        val binding = ItemFavoritBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.textNamaFavorit.text = item.nama_produk
        // Catatan kosong diganti teks pengingat agar kartu tidak terlihat kosong
        holder.binding.textCatatan.text = if (item.catatan.isEmpty()) {
            context.getString(R.string.favorit_tanpa_catatan)
        } else {
            item.catatan
        }

        holder.binding.btnUbahCatatan.setOnClickListener { onUbah(item) }
        holder.binding.btnHapusFavorit.setOnClickListener { onHapus(item) }
    }

    override fun getItemCount(): Int = items.size
}
