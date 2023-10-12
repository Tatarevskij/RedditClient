package com.example.redditclient.data.dto

import com.example.redditclient.data.adapters.AnyDataSealed
import com.example.redditclient.entity.Comment

class CommentsWithoutLinkDto(
    private val data: List<AnyDataSealed>
) {
    fun execute(): List<Comment> {
        val comments: MutableList<Comment> = mutableListOf()
        val listing: AnyDataSealed.Listing = data[1] as AnyDataSealed.Listing
        listing.data!!.children.forEach {
            val comment = createComment(it as AnyDataSealed.Comment)
            comments.add(comment)
        }
        return comments
    }

    fun createComment(comment: AnyDataSealed.Comment): Comment {
        return SealedCommentDto(
            kind = comment.kind,
            data = comment.data
        )
    }
}