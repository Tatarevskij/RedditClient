package com.example.redditclient.data.dto

import com.example.redditclient.entity.Me
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class MeDto @Inject constructor(
    @Json(name = "icon_img") override val iconImg: String,
    @Json(name = "name") override val name: String,
    @Json(name = "num_friends") override val numFriends: Int
): Me{
}