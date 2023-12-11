package com.capstone.project.trashhub.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListBankSampah (
    @field:SerializedName("Name")
    var name : String,

    @field:SerializedName("Fulladdress")
    var fullAddress : String,

    @field:SerializedName("Street")
    var street : String,

    @field:SerializedName("Categories")
    var categories : String,

    @field:SerializedName("Latitude")
    var latitude : String,

    @field:SerializedName("Longitudea")
    var longitude : String,

    @field:SerializedName("Place_Id")
    var id : String,

    @field:SerializedName("Opening_Hours")
    var openingHouse : String,

    @field:SerializedName("Google_Maps_URL")
    var googleMapURL : String,

    @field:SerializedName("Phones")
    var phones : String,

    @field:SerializedName("Municipality")
    var municipality : String,

    @field:SerializedName("Featured_Image")
    var imageUrl : String,

    ): Parcelable