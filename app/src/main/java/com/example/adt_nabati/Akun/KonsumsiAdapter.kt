package com.example.adt_nabati.Akun

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.adt_nabati.R
import com.example.adt_nabati.data.model.KonsumsiModel
import com.example.adt_nabati.databinding.ItemKonsumsiBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// Adapter daftar riwayat konsumsi, aksi ubah dan hapus dikirim ke Activity lewat lambda
class KonsumsiAdapter(
    private val items: List<KonsumsiModel>,
    private val onUbah: (KonsumsiModel) -> Unit,
    private val onHapus: (KonsumsiModel) -> Unit
) : RecyclerView.Adapter<KonsumsiAdapter.KonsumsiViewHolder>() {

    inner class KonsumsiViewHolder(val binding: ItemKonsumsiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KonsumsiViewHolder {
        val binding = ItemKonsumsiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KonsumsiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KonsumsiViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        // Total gizi = nilai per bungkus dikali jumlah bungkus
        val totalGula = item.gula * item.jumlah
        val totalNatrium = item.natrium * item.jumlah
        val totalLemak = item.lemak * item.jumlah

        holder.binding.textNamaKonsumsi.text = item.nama_produk
        holder.binding.textJumlah.text = context.getString(R.string.fmt_jumlah_bungkus, item.jumlah)
        holder.binding.textGiziKonsumsi.text =
            context.getString(R.string.fmt_gizi_konsumsi, totalGula, totalNatrium, totalLemak)
        holder.binding.textWaktu.text = formatWaktu(item.created_at)

        // Warna teks gizi mengikuti porsi gula terhadap batas harian 50 g
        val warna = if (totalGula >= 50) {
            R.color.merah_nabati
        } else if (totalGula >= 25) {
            R.color.oranye_waspada
        } else {
            R.color.hijau_aman
        }
        holder.binding.textGiziKonsumsi.setTextColor(ContextCompat.getColor(context, warna))

        holder.binding.btnUbah.setOnClickListener { onUbah(item) }
        holder.binding.btnHapus.setOnClickListener { onHapus(item) }
    }

    override fun getItemCount(): Int = items.size

    // created_at dari Supabase berformat UTC, diubah ke jam lokal agar mudah dibaca
    private fun formatWaktu(createdAt: String?): String {
        if (createdAt == null || createdAt.length < 19) {
            return ""
        }
        return try {
            val formatAsal = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            formatAsal.timeZone = TimeZone.getTimeZone("UTC")
            val tanggal = formatAsal.parse(createdAt.substring(0, 19))
            val formatTampil = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
            formatTampil.format(tanggal!!)
        } catch (e: Exception) {
            createdAt.substring(0, 16).replace("T", " ")
        }
    }
}
