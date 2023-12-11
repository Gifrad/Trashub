package com.capstone.project.trashhub.network.api

import com.capstone.project.trashhub.network.model.BankSampahResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("data")
    fun getDataBankSampah(
    ): Call<BankSampahResponse>

}