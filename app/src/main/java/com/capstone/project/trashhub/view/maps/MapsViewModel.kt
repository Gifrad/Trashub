package com.capstone.project.trashhub.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.project.trashhub.network.api.ApiConfig
import com.capstone.project.trashhub.network.model.BankSampahResponse
import com.capstone.project.trashhub.network.model.ListBankSampah
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel : ViewModel() {

    private val _listBankSampah = MutableLiveData<ArrayList<ListBankSampah>>()
    val listBankSampah : LiveData<ArrayList<ListBankSampah>> get() = _listBankSampah

    fun getBankSampahLocation(){
        val bankSampahList = ArrayList<ListBankSampah>()
        val client = ApiConfig.getApiService().getDataBankSampah()

        client.enqueue(object : Callback<BankSampahResponse> {
            override fun onResponse(call: Call<BankSampahResponse>, response: Response<BankSampahResponse>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    Log.d("MapsActivity",responseBody.toString())
                    _listBankSampah.postValue(responseBody.data)
                    _listBankSampah.value = bankSampahList
                }else{
                    Log.d("MapsActivity","onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BankSampahResponse>, t: Throwable) {
                Log.e("MapsActivity", "onFailure: ${t.message}")
            }
        })

    }

}