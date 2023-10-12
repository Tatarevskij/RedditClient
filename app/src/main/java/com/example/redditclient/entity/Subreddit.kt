package com.example.redditclient.entity

interface Subreddit {
    val bannerImg: String
    val bannerBackgroundImage: String?
    val communityIcon: String?
    val title: String
    val iconImg: String
    val subscribers: Int
    val name: String
    val userIsSubscriber: Boolean
    val publicDescription: String
    val displayNamePrefixed: String
}