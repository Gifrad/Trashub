package com.capstone.project.trashhub.view.detailbanksampah

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.project.trashhub.view.detailbanksampah.fragment.DetailBankSampahFragment

class SectionsPagerAdapter(activity: AppCompatActivity, data: Bundle) : FragmentStateAdapter(activity) {

    private val fragmentBundle : Bundle

    init {
        fragmentBundle = data
    }
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DetailBankSampahFragment()
        }
        fragment?.arguments = fragmentBundle
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 1
    }
}