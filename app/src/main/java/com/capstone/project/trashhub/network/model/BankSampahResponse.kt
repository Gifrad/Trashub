package com.capstone.project.trashhub.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BankSampahResponse(
    @field:SerializedName("success")
    var success: String,
    @field:SerializedName("data")
    var data: ArrayList<ListBankSampah>
): Parcelable