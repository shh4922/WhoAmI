package com.mnu.whoami

import android.telecom.Call
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface APIservice {

    //이미지파일 전송
    @Multipart
    @POST("getImgFromApp")
    fun SendToServer_faceimg(@Part file: MultipartBody.Part): retrofit2.Call<FaceImgResponse>

}