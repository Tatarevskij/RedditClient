package com.example.redditclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.redditclient.entity.CommentInDb
import com.example.redditclient.entity.NewCommentInDb

@Dao
interface CommentDao {
    @Transaction
    @Query("SELECT * FROM comments")
    suspend fun getAllComments(): List<CommentInDb>

    @Query("DELETE FROM comments WHERE name = :name")
    suspend fun deleteComment(name: String)

    @Insert(entity = CommentInDb::class)
    suspend fun insertComment (comment: NewCommentInDb)

    @Query("DELETE FROM comments")
    suspend fun deleteAllComments()
}