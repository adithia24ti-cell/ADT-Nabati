package com.example.adt_nabati.Beranda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adt_nabati.R
import com.example.adt_nabati.databinding.ItemProdukGridBinding

// Penghubung data produk dengan kotak grid di Beranda
class ProdukGridAdapter(
    private val produkList: List<ProdukModel>,
    private val onItemClick: (ProdukModel) -> Unit
) : RecyclerView.Adapter<ProdukGridAdapter.ProdukViewHolder>() {

    inner class ProdukViewHolder(val binding: ItemProdukGridBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukViewHolder {
        val binding = ItemProdukGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProdukViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukViewHolder, position: Int) {
        val produk = produkList[position]
        val context = holder.itemView.context
        with(holder.binding) {
            textNamaGrid.text = produk.nama
            textInfoGrid.text = context.getString(R.string.fmt_info_grid, produk.energi, produk.takaran)

            Glide.with(context)
                .load(produk.gambar)
                .into(imgProdukGrid)

            // Status gula per bungkus memakai aturan 5/20
            val persenGula = produk.gula * 100 / 50
            if (persenGula <= 5) {
                textStatusGula.text = context.getString(R.string.status_gula_rendah, persenGula)
                textStatusGula.setTextColor(context.getColor(R.color.hijau_aman))
            } else if (persenGula < 20) {
                textStatusGula.text = context.getString(R.string.status_gula_sedang, persenGula)
                textStatusGula.setTextColor(context.getColor(R.color.oranye_waspada))
            } else {
                textStatusGula.text = context.getString(R.string.status_gula_tinggi, persenGula)
                textStatusGula.setTextColor(context.getColor(R.color.merah_nabati))
            }

            root.setOnClickListener {
                onItemClick(produk)
            }
        }
    }

    override fun getItemCount(): Int = produkList.size
}
