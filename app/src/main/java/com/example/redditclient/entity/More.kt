package com.example.redditclient.entity

interface More {
    val kind: String
    val data: MoreData
}

interface MoreData {
    val count: Int
    val children: List<String>
}