package com.example.redditclient.data.dto

import com.example.redditclient.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class LinkDto @Inject constructor(
    @Json(name = "data") override val data: DataDto
) : Link {
    @JsonClass(generateAdapter = true)
    data class DataDto(
        @Json(name = "subreddit") override val subreddit: String?,
        @Json(name = "saved") override val saved: Boolean?,
        @Json(name = "selftext") override val selftext: String?,
        @Json(name = "title") override val title: String?,
        @Json(name = "media_metadata") override val gallery: GalleryDto?,
        @Json(name = "name") override val name: String,
        @Json(name = "author_fullname") override val authorFullname: String?,
        @Json(name = "thumbnail") override val thumbnail: String?,
        @Json(name = "created") override val created: Double?,
        @Json(name = "url_overridden_by_dest") override val urlOverriddenByDest: String?,
        @Json(name = "preview") override val preview: PreviewDto?,
        @Json(name = "num_comments") override val numComments: Int?,
        @Json(name = "author") override val author: String?,
        @Json(name = "media") override val media: MediaDto?,
        @Json(name = "id") override val id: String,
        @Json(name = "is_video") override val is_video: Boolean?,
        @Json(name = "crosspost_parent_list") override val crosspostParentList: List<DataDto>?,
        @Json(name = "sr_detail") override val srDetail: SrDetailDto?,
        @Json(name = "url") override val url: String?
    ) : Data {
        @JsonClass(generateAdapter = true)
        data class PreviewDto(
            @Json(name = "images") override val images: List<ImageDto>
        ) : Preview

        @JsonClass(generateAdapter = true)
        data class MediaDto(
            @Json(name = "reddit_video") override val redditVideo: RedditVideoDto?
        ): Media {
            @JsonClass(generateAdapter = true)
            data class RedditVideoDto(
                @Json(name = "fallback_url") override val fallbackUrl: String?
            ): RedditVideo
        }
        @JsonClass(generateAdapter = true)
        data class SrDetailDto(
            @Json(name = "user_is_subscriber") override val userIsSubscriber: Boolean
        ):SrDetail
    }
}

@JsonClass(generateAdapter = true)
data class GalleryDto @Inject constructor(
    override val images: List<String>
): Gallary

@JsonClass(generateAdapter = true)
data class ImageDto @Inject constructor(
    @Json(name = "resolutions") override val resolutions: List<ResolutionDto>,
    @Json(name = "variants") override val variants: VariantsDto?
) : Image

@JsonClass(generateAdapter = true)
data class ResolutionDto @Inject constructor(
    @Json(name = "url") override val url: String
) : Resolution

@JsonClass(generateAdapter = true)
data class VariantsDto @Inject constructor(
    @Json(name = "mp4") override val mp4: Mp4Dto?
): Variants

@JsonClass(generateAdapter = true)
data class Mp4Dto @Inject constructor(
    @Json(name = "resolutions") override val resolutions: List<ResolutionDto>
): Mp4

@JsonClass(generateAdapter = true)
data class LinkListingDto @Inject constructor(
    @Json(name = "data") override val data: LinkListingDataDto
) : LinkListing {
    @JsonClass(generateAdapter = true)
    data class LinkListingDataDto(
        @Json(name = "after") override val after: String?,
        @Json(name = "children") override val children: List<LinkDto>
    ) : LinkListingData
}