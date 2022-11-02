package com.rich.multipartexercise.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rich.multipartexercise.data.remote.APIClient
import com.rich.multipartexercise.data.remote.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    private val _registerUser = MutableLiveData<RegisterResponse>()
    val registerUser : LiveData<RegisterResponse> = _registerUser

    fun registerUser(fullName : RequestBody, email : RequestBody, password : RequestBody, phoneNumber : RequestBody, address : RequestBody, city : RequestBody, image : MultipartBody.Part) {
        val client = APIClient.getService().registerUser(fullName, email, password, phoneNumber, address, city, image)
        client.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if(response.isSuccessful){
                    val data = response.body()
                    if(data != null){
                        _registerUser.postValue(data)
                    }else{
                        _registerUser.postValue(null)
                    }
                }else{
                    _registerUser.postValue(null)
                    Log.d("Error", response.message())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("Error", t.message!!)
                _registerUser.postValue(null)
            }

        })
    }
}