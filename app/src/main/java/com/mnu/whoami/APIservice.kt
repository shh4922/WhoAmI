package com.mnu.whoami

import android.telecom.Call
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

public interface APIservice {

    //이미지 데이터 전송
    @Multipart
    @POST("getImgFromApp")
    fun SendToServer_faceimg(
        @Part image: MultipartBody.Part?
    ): Call<FaceImgResponse>
}