package com.example.adt_nabati.Beranda

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// Menentukan fragment untuk tiap tab Batas Harian
class BatasTabsAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BatasGulaFragment()
            1 -> BatasNatriumFragment()
            2 -> BatasLemakFragment()
            else -> throw IllegalStateException("Posisi tab tidak valid")
        }
    }
}
