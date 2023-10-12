package com.example.redditclient.data.dto

import com.example.redditclient.entity.Subreddit
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
class SubredditDto @Inject constructor(
    @Json(name = "banner_img") override val bannerImg: String,
    @Json(name = "banner_background_image") override val bannerBackgroundImage: String?,
    @Json(name = "community_icon") override val communityIcon: String?,
    @Json(name = "title") override val title: String,
    @Json(name = "icon_img") override val iconImg: String,
    @Json(name = "subscribers") override val subscribers: Int,
    @Json(name = "name") override val name: String,
    @Json(name = "user_is_subscriber") override val userIsSubscriber: Boolean,
    @Json(name = "public_description") override val publicDescription: String,
    @Json(name = "display_name_prefixed") override val displayNamePrefixed: String,
): Subreddit {
}