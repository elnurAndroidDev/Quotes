package com.example.quotes.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.quotes.ui.fragments.FavoritesFragment
import com.example.quotes.ui.fragments.HomeFragment

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int) = when (position) {
        0 -> HomeFragment()
        1 -> FavoritesFragment()
        else -> Fragment()
    }
}