package com.example.redditclient.data.dto

import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.CommentData
import com.example.redditclient.entity.CommentInDb
import com.example.redditclient.entity.CommentListing

data class CommentDbToCommentDto(
    private val commentInDb: CommentInDb,
    override val kind: String? = "t1",
    override val data: CommentDataDto = CommentDataDto(
        name = commentInDb.name,
        saved = true,
        createdUtc = commentInDb.createdUtc,
        bodyHtml = commentInDb.bodyHtml,
        author = commentInDb.author,
        replies = null,
        authorFullName = null,
        body = null,
        children = null,
        count = null,
        depth = null,
        id = "",
        parentId = "",
        linkId = null
    )

) : Comment

data class CommentDataDto(
    override val saved: Boolean?,
    override val replies: CommentListing?,
    override val createdUtc: Double?,
    override val body: String?,
    override val parentId: String,
    override val author: String?,
    override val authorFullName: String?,
    override val children: List<String>?,
    override val count: Int?,
    override val id: String,
    override val bodyHtml: String?,
    override val depth: Int?,
    override val name: String?,
    override val linkId: String?

): CommentData
