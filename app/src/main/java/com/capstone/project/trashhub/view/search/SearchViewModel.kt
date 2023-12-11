package com.capstone.project.trashhub.view.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project.trashhub.network.api.ApiConfig
import com.capstone.project.trashhub.network.model.BankSampahResponse
import com.capstone.project.trashhub.network.model.ListBankSampah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    val listBankSamapah = MutableLiveData<ArrayList<ListBankSampah>>()

    fun showDataSearch() {
        val listData = ArrayList<ListBankSampah>()
        ApiConfig.getApiService()
            .getDataBankSampah()
            .enqueue(object : Callback<BankSampahResponse> {
                override fun onResponse(
                    call: Call<BankSampahResponse>,
                    response: Response<BankSampahResponse>
                ) {
                    val dataList = response.body()
                    if (response.isSuccessful && dataList != null) {
                        Log.d("SearchActivity : ",response.message())
                        listBankSamapah.postValue(dataList.data)
                        listBankSamapah.value = listData
                    } else {
                        Log.d("SearchActivity :", response.message())
                        Log.e("SearchActivity :", "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<BankSampahResponse>, t: Throwable) {
                    Log.e("SearchActivity:", "onFailure: ${t.message}")
                }

            })

    }
}