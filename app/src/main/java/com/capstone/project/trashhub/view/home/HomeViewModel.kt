package com.capstone.project.trashhub.view.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project.trashhub.network.api.ApiConfig
import com.capstone.project.trashhub.network.model.BankSampahResponse
import com.capstone.project.trashhub.network.model.ListBankSampah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel: ViewModel() {

    val listBankSampah = MutableLiveData<ArrayList<ListBankSampah>>()

    fun getBankSampah() {
        ApiConfig.getApiService().getDataBankSampah().enqueue(object :
            Callback<BankSampahResponse> {
            override fun onResponse(
                call: Call<BankSampahResponse>,
                response: Response<BankSampahResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        listBankSampah.postValue(responseBody.data)
                        Log.d(TAG, "onResponseSuccess: ${responseBody.data}")
                    }
                } else {
                    Log.d(TAG, "onResponseNotSuccess: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BankSampahResponse>, t: Throwable) {
                Log.d(TAG, "onResponse: ${t.message}")
            }
        })
    }

    companion object{
        const val TAG = "HomeViewModel"
    }
}
