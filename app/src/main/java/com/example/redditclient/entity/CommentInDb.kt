package com.example.redditclient.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentInDb(
    @PrimaryKey
    @ColumnInfo(name = "db_id")
    val dbId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "saved")
    val saved: Boolean,
    @ColumnInfo(name = "created_utc")
    val createdUtc: Double,
    @ColumnInfo(name = "body")
    val bodyHtml: String,
    @ColumnInfo(name = "author")
    val author: String,
)
