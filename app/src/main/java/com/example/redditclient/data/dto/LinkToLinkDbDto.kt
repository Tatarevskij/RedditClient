package com.example.redditclient.data.dto

import com.example.redditclient.entity.Data
import com.example.redditclient.entity.Link
import com.example.redditclient.entity.NewLinkInDb

class LinkToLinkDbDto(
    private val link: Link
) {
    fun execute(): NewLinkInDb {
        return NewLinkInDb(
            id = link.data.id,
            name = link.data.name,
            authorFullName = link.data.authorFullname,
            title = link.data.title,
            subreddit = link.data.subreddit,
            author = link.data.author,
            numComments = link.data.numComments,
            created = link.data.created,
            url = getMediaUrl(link.data)
        )
    }

    private fun getMediaUrl(data: Data): String? {
        if (data.preview?.images?.get(0)?.variants?.mp4 != null) {
            return try {
                data.urlOverriddenByDest
            } catch (ex: Exception) {
                null
            }
        }
        if (data.preview?.images?.get(0)?.resolutions?.get(1)?.url != null) {
            return try {
                data.preview!!.images[0].resolutions[1].url
            } catch (ex: Exception) {
                null
            }
        }
        if (!data.url.isNullOrEmpty()) {
            return try {
                data.url
            } catch (ex: Exception) {
                null
            }
        }
        return null
    }
}