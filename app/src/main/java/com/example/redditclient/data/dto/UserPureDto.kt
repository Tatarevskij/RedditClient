package com.example.redditclient.data.dto

import com.example.redditclient.entity.UserPure
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class UserPureDto @Inject constructor(
    @Json(name = "id") override val id: String?,
    @Json(name = "name") override val name: String,
    @Json(name = "profile_img") override val profileImg: String,
) : UserPure