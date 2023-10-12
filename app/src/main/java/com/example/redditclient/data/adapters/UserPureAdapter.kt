package com.example.redditclient.data.adapters

import com.example.redditclient.data.dto.UserPureDto
import com.squareup.moshi.*

class UserPureAdapter  {
    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
        delegate: JsonAdapter<UserPureDto>
    ): UserPureDto? {
        return if (jsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            jsonReader.beginObject()
            jsonReader.nextName()
            val json = delegate.fromJson(jsonReader)
            jsonReader.endObject()
            json
        } else {
            jsonReader.endObject()
            delegate.fromJson(jsonReader)
        }
    }

    @ToJson
    fun toJson(
        jsonWriter: JsonWriter,
        data: UserPureDto,
        delegate: JsonAdapter<UserPureDto>
    ) {
        throw Exception("no data")
    }
}

