package com.example.redditclient.entity

interface UserFull {
    val id: String
    val isFriend: Boolean
    val subreddit: Subreddit
    val iconImg: String
    val userName: String
}