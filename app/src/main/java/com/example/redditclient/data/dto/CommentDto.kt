package com.example.redditclient.data.dto

import com.example.redditclient.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import javax.inject.Inject

@JsonClass(generateAdapter = true)
data class CommentDto @Inject constructor(
    @Json(name = "kind") override val kind: String,
    @Json(name = "data") override val data: CommentDataDto?
) : Comment {
    @JsonClass(generateAdapter = true)
    data class CommentDataDto(
        @Json(name = "saved") override val saved: Boolean?,
        @Json(name = "replies") override val replies: RepliesDto?,
        @Json(name = "created_utc") override val createdUtc: Double?,
        @Json(name = "body") override val body: String?,
        @Json(name = "parent_id") override val parentId: String,
        @Json(name = "author") override val author: String?,
        @Json(name = "author_fullname") override val authorFullName: String?,
        @Json(name = "children") override val children: List<String>?,
        @Json(name = "count") override val count: Int?,
        @Json(name = "id") override val id: String,
        @Json(name = "body_html") override val bodyHtml: String?,
        @Json(name = "depth") override val depth: Int?,
        @Json(name = "name") override val name: String?,
        @Json(name = "link_id") override val linkId: String?
    ) : CommentData
}

@JsonClass(generateAdapter = true)
data class CommentListingDto @Inject constructor(
    @Json(name = "data") override val data: CommentListingDataDto
) : CommentListing {
    @JsonClass(generateAdapter = true)
    data class CommentListingDataDto(
        @Json(name = "children") override val children: List<CommentDto>?
    ) : CommentListingData
}

@JsonClass(generateAdapter = true)
data class RepliesDto(
    override val data: CommentListingDto.CommentListingDataDto
) : CommentListing

@JsonClass(generateAdapter = true)
data class MoreDataDto(
   override val count: Int,
   override val children: List<String>
): MoreData

@JsonClass(generateAdapter = true)
data class MoreChildrenDto(
    override val json: JsonDto
) : MoreChildren {
    @JsonClass(generateAdapter = true)
    data class JsonDto(
        override val data: JsonDataDto
    ) : com.example.redditclient.entity.Json {
        @JsonClass(generateAdapter = true)
        data class JsonDataDto(
            override val things: List<CommentDto>
        ) : JsonData
    }
}


/*@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Replies*/

/*class CommentRepliesAdapter {
    @Replies
    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
        delegate: JsonAdapter<CommentListingSealed>
    ): CommentListingSealed? {
        println(jsonReader.peek())
        if (jsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            println(jsonReader.path)
            return delegate.fromJson(jsonReader)
        } else
            println(jsonReader.path)
        jsonReader.nextString()
        return null
    }

    @ToJson
    fun toJson(
        jsonWriter: JsonWriter,
        data: RepliesSealed,
        delegateTrue: JsonAdapter<CommentListingSealed>
    ) {
        throw Exception("no data to convert")
    }
}*/

