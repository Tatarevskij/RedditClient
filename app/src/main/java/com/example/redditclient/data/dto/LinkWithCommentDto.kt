package com.example.redditclient.data.dto

import com.example.redditclient.data.adapters.AnyDataSealed
import com.example.redditclient.entity.*

class LinkWithCommentDto (
    private val data: List<AnyDataSealed>
        ) {
    fun execute(): Pair<Link, List<Comment>> {
        val comments: MutableList<Comment> = mutableListOf()
        var listing = data[0] as AnyDataSealed.Listing
        val sealedLink = listing.data?.children?.get(0) as AnyDataSealed.Link
        val link = SealedLinkDto(sealedLink.data)
        listing = data[1] as AnyDataSealed.Listing
        listing.data!!.children.forEach {
            val comment = createComment( it as AnyDataSealed.Comment)
            comments.add(comment)
        }
        return Pair(link, comments)
    }
}

fun createComment(comment: AnyDataSealed.Comment): Comment {
    return SealedCommentDto(
        kind = comment.kind,
        data = comment.data
    )
}

data class SealedLinkDto(
    override val data: Data
): Link

data class SealedCommentDto(
    override val kind: String?,
    override val data: CommentData?
): Comment