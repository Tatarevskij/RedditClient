package com.example.redditclient.entity

interface Comment {
    val kind: String?
    val data: CommentData?
}

interface CommentData {
    val saved: Boolean?
    val replies: CommentListing?
    val createdUtc: Double?
    val body: String?
    val parentId: String
    val author: String?
    val authorFullName: String?
    val children: List<String>?
    val count: Int?
    val id: String
    val bodyHtml: String?
    val depth: Int?
    val name: String?
    val linkId: String?
}

interface CommentListing {
    val data: CommentListingData
}

interface CommentListingData {
    val children: List<Comment>?
}

interface MoreChildren {
    val json: Json
}

interface Json {
    val data: JsonData
}

interface JsonData {
    val things: List<Comment>?
}

