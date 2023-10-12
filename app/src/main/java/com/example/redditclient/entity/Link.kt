package com.example.redditclient.entity

interface Link {
    val data: Data
}

interface Data {
    val subreddit: String?
    val saved: Boolean?
    val selftext: String?
    val title: String?
    val gallery: Gallary?
    val name: String
    val authorFullname: String?
    val thumbnail: String?
    val created: Double?
    val urlOverriddenByDest: String?
    val preview: Preview?
    val numComments: Int?
    val author: String?
    val media: Media?
    val id: String
    val is_video: Boolean?
    val crosspostParentList: List<Data>?
    val srDetail: SrDetail?
    val url: String?
}

interface Gallary {
    val images: List<String>
}

interface Preview {
    val images: List<Image>
}

interface Image {
    val resolutions: List<Resolution>
    val variants: Variants?
}

interface Resolution {
    val url: String
}

interface Variants {
    val mp4: Mp4?
}

interface Mp4 {
    val resolutions: List<Resolution>
}

interface Media {
    val redditVideo: RedditVideo?
}

interface RedditVideo {
    val fallbackUrl: String?
}

interface SrDetail {
    val userIsSubscriber: Boolean
}

interface LinkListing {
    val data: LinkListingData
}

interface LinkListingData {
    val after: String?
    val children: List<Link>
}