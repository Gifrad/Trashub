package com.capstone.project.trashhub.view.detailbanksampah.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.network.model.ListBankSampah
import com.capstone.project.trashhub.view.detailbanksampah.DetailBankSampahActivity

class DetailBankSampahFragment : Fragment(R.layout.fragment_detail_bank_sampah) {

    private lateinit var alamatLengkap :TextView
    private lateinit var nomerTelepon :TextView
    private lateinit var waktuBuka :TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alamatLengkap = view.findViewById(R.id.tv_alamat_lengkap_banksampah_detail)
        nomerTelepon = view.findViewById(R.id.tv_no_telepon_detail)
        waktuBuka = view.findViewById(R.id.tv_waktu_buka_detail)

        val arg =arguments
        val data = arg?.getParcelable<ListBankSampah>(EXTRA_DATA_ALAMAT_FRAGMENT)
        Log.d(TAG, "onViewCreated: ${data?.id}")

        alamatLengkap.text = data?.fullAddress
        nomerTelepon.text = data?.phones
        waktuBuka.text = data?.openingHouse?.replace(",","\n")?.prependIndent("-")


    }

    companion object{
        const val TAG = "DetailBankSampahFragment"
        const val EXTRA_DATA_ALAMAT_FRAGMENT = "EXTRA_DATA_FRAGMENT"
    }
}