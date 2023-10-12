package com.example.redditclient.data.dto

import com.example.redditclient.entity.UserFull
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class UserFullDto @Inject constructor(
    @Json(name = "id") override val id: String,
    @Json(name = "is_friend") override val isFriend: Boolean,
    @Json(name = "subreddit") override val subreddit: SubredditDto,
    @Json(name = "icon_img") override val iconImg: String,
    @Json(name = "name") override val userName: String
): UserFull {
}