package com.rich.multipartexercise.data.remote

import com.rich.multipartexercise.data.remote.model.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {
    @POST("auth/register")
    @Multipart
    fun registerUser(
        @Part("full_name") fullName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("address") address: RequestBody,
        @Part("city") city : RequestBody,
        @Part image : MultipartBody.Part
    ) : Call<RegisterResponse>
}