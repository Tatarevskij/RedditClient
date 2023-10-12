package com.example.redditclient.data.adapters

import com.example.redditclient.data.dto.*
import com.squareup.moshi.JsonClass

sealed class AnyDataSealed {
    @JsonClass(generateAdapter = true)
    data class Comment(
        val kind: String,
        val data: CommentDto.CommentDataDto
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class Link(
        val kind: String,
        val data: LinkDto.DataDto
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class UserFull(
        val data: UserFullDto
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class Subreddit(
        val data: SubredditDto
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class More(
        val data: MoreDataDto
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class Listing(
        val kind: String,
        val data: ListingDataDto?
    ) : AnyDataSealed() {
        @JsonClass(generateAdapter = true)
        data class ListingDataDto(
            val children: List<AnyDataSealed>
        )
    }
    @JsonClass(generateAdapter = true)
    data class Empty(
        val data: String?
    ) : AnyDataSealed()

    @JsonClass(generateAdapter = true)
    data class UserList(
        val data: ListingDataDto?
    ) : AnyDataSealed() {
        @JsonClass(generateAdapter = true)
        data class ListingDataDto(
            val children: List<UserDto>
        ) {
            @JsonClass(generateAdapter = true)
            data class UserDto(
                val name: String,
                val id: String
            )
        }
    }
}