package com.example.redditclient.data.dto

import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.NewCommentInDb

class CommentToCommentDbDto(
    private val comment: Comment
) {
    fun execute() : NewCommentInDb {
        return NewCommentInDb(
            name = comment.data?.name,
            saved = comment.data?.saved,
            createdUtc = comment.data?.createdUtc,
            bodyHtml = comment.data?.bodyHtml,
            author = comment.data?.author
        )
    }
}