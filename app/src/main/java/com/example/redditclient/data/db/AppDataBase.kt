package com.example.redditclient.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.redditclient.entity.CommentInDb
import com.example.redditclient.entity.LinkInDb

@Database(entities = [LinkInDb::class, CommentInDb::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLinkDao(): LinkDao
    abstract fun getCommentDao(): CommentDao
}