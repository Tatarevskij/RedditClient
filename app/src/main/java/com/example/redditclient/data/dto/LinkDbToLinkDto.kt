package com.example.redditclient.data.dto

import com.example.redditclient.entity.*

data class LinkDbToLinkDto(
    private val link: LinkInDb,
    override val data: DataDto = DataDto(
        subreddit = link.subreddit,
        saved = true,
        selftext = null,
        title = link.title,
        gallery = null,
        name = link.name,
        authorFullname = link.authorFullName,
        thumbnail = null,
        created = link.created,
        urlOverriddenByDest = null,
        preview = null,
        numComments = link.numComments,
        author = link.author,
        media = null,
        id = link.id,
        is_video = null,
        crosspostParentList = null,
        srDetail = null,
        url = link.url
    )
) : Link {
    data class DataDto(
        override val subreddit: String?,
        override val saved: Boolean?,
        override val selftext: String?,
        override val title: String?,
        override val gallery: Gallary?,
        override val name: String,
        override val authorFullname: String?,
        override val thumbnail: String?,
        override val created: Double?,
        override val urlOverriddenByDest: String?,
        override val preview: Preview?,
        override val numComments: Int?,
        override val author: String?,
        override val media: Media?,
        override val id: String,
        override val is_video: Boolean?,
        override val crosspostParentList: List<Data>?,
        override val srDetail: SrDetail?,
        override val url: String?

    ): Data
}