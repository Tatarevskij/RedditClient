package com.example.redditclient.entity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey


data class NewLinkInDb(
    @PrimaryKey
    @ColumnInfo(name = "db_id")
    val dbId: Int? = null,
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "author_full_name")
    val authorFullName: String? = null,
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "subreddit")
    val subreddit: String? = null,
    @ColumnInfo(name = "author")
    val author: String? = null,
    @ColumnInfo(name = "num_comments")
    val numComments: Int? = null,
    @ColumnInfo(name = "created")
    val created: Double?,
    @ColumnInfo(name = "url")
    val url: String? = null,
)