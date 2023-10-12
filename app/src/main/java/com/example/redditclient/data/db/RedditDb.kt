package com.example.redditclient.data.db

import com.example.redditclient.entity.CommentInDb
import com.example.redditclient.entity.LinkInDb
import com.example.redditclient.entity.NewCommentInDb
import com.example.redditclient.entity.NewLinkInDb
import javax.inject.Inject

class RedditDb @Inject constructor(
    private val linkDao: LinkDao,
    private val commentDao: CommentDao
) {
    suspend fun getAllLinks(): List<LinkInDb> {
        return linkDao.getAllLinks()
    }

    suspend fun addLink(linkInDb: NewLinkInDb) {
        linkDao.insertLink(linkInDb)
    }

    suspend fun deleteLink(name: String) {
        linkDao.deleteLink(name)
    }

    suspend fun getAllComments(): List<CommentInDb> {
        return commentDao.getAllComments()
    }

    suspend fun addComment(commentInDb: NewCommentInDb) {
        commentDao.insertComment(commentInDb)
    }

    suspend fun deleteComment(name: String) {
        commentDao.deleteComment(name)
    }

    suspend fun clear() {
        linkDao.deleteAllLinks()
        commentDao.deleteAllComments()
    }
}