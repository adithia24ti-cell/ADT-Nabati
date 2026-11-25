package com.example.adt_nabati.Jelajah

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adt_nabati.R
import com.example.adt_nabati.data.model.ProdukDuniaModel
import com.example.adt_nabati.databinding.ItemProdukDuniaBinding

// Adapter galeri produk dari Open Food Facts, pola sama dengan PhotoAdapter di modul
class ProdukDuniaAdapter(
    private val items: List<ProdukDuniaModel>,
    private val modeHorizontal: Boolean,
    private val onKlik: (ProdukDuniaModel) -> Unit
) : RecyclerView.Adapter<ProdukDuniaAdapter.ProdukDuniaViewHolder>() {

    inner class ProdukDuniaViewHolder(val binding: ItemProdukDuniaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdukDuniaViewHolder {
        val binding = ItemProdukDuniaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pada mode horizontal kartu diberi lebar tetap 160dp agar bisa digeser ke samping
        if (modeHorizontal) {
            binding.root.layoutParams.width = (160 * parent.resources.displayMetrics.density).toInt()
        }
        return ProdukDuniaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdukDuniaViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.textNamaDunia.text = item.product_name
        holder.binding.textMerekDunia.text = item.brands ?: "-"

        Glide.with(context)
            .load(item.image_front_small_url)
            .into(holder.binding.imgProdukDunia)

        // Gula per 100 g, warna mengikuti kategori tinggi/sedang/rendah
        val gula = item.nutriments?.sugars_100g
        if (gula == null) {
            holder.binding.textGulaDunia.text = context.getString(R.string.dunia_gula_kosong)
            holder.binding.textGulaDunia.setTextColor(ContextCompat.getColor(context, R.color.abu_gelap))
        } else {
            holder.binding.textGulaDunia.text = context.getString(R.string.dunia_gula, gula)
            val warna = if (gula > 22.5) {
                R.color.merah_nabati
            } else if (gula > 5) {
                R.color.oranye_waspada
            } else {
                R.color.hijau_aman
            }
            holder.binding.textGulaDunia.setTextColor(ContextCompat.getColor(context, warna))
        }

        // Nutri-Score A sampai E, tanda tanya jika belum dinilai
        val nilai = item.nutriscore_grade ?: "unknown"
        val warnaSkor = when (nilai) {
            "a", "b" -> R.color.hijau_aman
            "c" -> R.color.kuning_nabati
            "d" -> R.color.oranye_waspada
            "e" -> R.color.merah_nabati
            else -> R.color.abu_gelap
        }
        holder.binding.textNutriscore.text = if (nilai.length == 1) nilai.uppercase() else "?"
        holder.binding.textNutriscore.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, warnaSkor))

        holder.binding.root.setOnClickListener { onKlik(item) }
    }

    override fun getItemCount(): Int = items.size
}
