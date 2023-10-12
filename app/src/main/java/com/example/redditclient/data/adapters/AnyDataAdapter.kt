package com.example.redditclient.data.adapters

import com.squareup.moshi.*

class AnyDataAdapter {
    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
        delegateLink: JsonAdapter<AnyDataSealed.Link>,
        delegateComment: JsonAdapter<AnyDataSealed.Comment>,
        delegateListing: JsonAdapter<AnyDataSealed.Listing>,
        delegateUserFull: JsonAdapter<AnyDataSealed.UserFull>,
        delegateEmpty: JsonAdapter<AnyDataSealed.Empty>,
        delegateUserList: JsonAdapter<AnyDataSealed.UserList>,
        delegateSubreddit: JsonAdapter<AnyDataSealed.Subreddit>
        ): AnyDataSealed? {
        val json = jsonReader.peekJson()
        if (json.peek() == JsonReader.Token.STRING) {
            return null
        }
        json.beginObject()
        json.nextName()

       when (json.readJsonValue()) {
            "Listing" -> {
                println("Listing")
                return delegateListing.fromJson(jsonReader)
            }
            "t1" -> {
                println("Comment")
                return delegateComment.fromJson(jsonReader)
            }
           "t2" -> {
               println("User")
               return delegateUserFull.fromJson(jsonReader)
           }
           "t3" -> {
               println("Link")
               return delegateLink.fromJson(jsonReader)
           }
           "t5" -> {
               println("Subreddit")
               return delegateSubreddit.fromJson(jsonReader)
           }
           "more" -> {
               println("More")
               return delegateComment.fromJson(jsonReader)
           }
           "UserList" -> {
               println("UserList")
               return delegateUserList.fromJson(jsonReader)
           }

        }
        return delegateEmpty.fromJson(jsonReader)
    }

    @ToJson
    fun toJson(
        jsonWriter: JsonWriter,
        data: AnyDataSealed,
        delegateLink: JsonAdapter<AnyDataSealed.Link>,
        delegateComment: JsonAdapter<AnyDataSealed.Comment>
    ) {
        throw Exception("no data")
    }
}



