package com.mnu.whoami

import com.google.gson.annotations.SerializedName

/***
 * 서버로부터 오는 응답
 */
data class FaceImgResponse(
    @SerializedName("name")
    var name: String
)


