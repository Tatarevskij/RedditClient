package com.example.redditclient.entity

import androidx.room.ColumnInfo

data class NewCommentInDb(
    @ColumnInfo(name = "db_id")
    val dbId: Int? = null,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "saved")
    val saved: Boolean?,
    @ColumnInfo(name = "created_utc")
    val createdUtc: Double?,
    @ColumnInfo(name = "body")
    val bodyHtml: String?,
    @ColumnInfo(name = "author")
    val author: String?,
)
