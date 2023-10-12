package com.example.redditclient.data.adapters

import com.example.redditclient.data.dto.RepliesDto
import com.squareup.moshi.*

class RepliesAdapter {
    @ToJson
    fun toJson(
        jsonWriter: JsonWriter,
        data: RepliesDto,
        delegateTrue: JsonAdapter<RepliesDto>
    ) {
        throw Exception("no data to convert")
    }

    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
        delegate: JsonAdapter<RepliesDto>
    ): RepliesDto? {
        if (jsonReader.peek() == JsonReader.Token.BEGIN_OBJECT) {
            return delegate.fromJson(jsonReader)
        } else
        jsonReader.nextString()
        return null
    }
}
