package com.example.redditclient.data.adapters

import com.example.redditclient.data.dto.GalleryDto
import com.example.redditclient.data.dto.UserPureDto
import com.squareup.moshi.*

class GalleryAdapter {
    @FromJson
    fun fromJson(
        jsonReader: JsonReader,
    ): GalleryDto {
        val images = mutableListOf<String>()
        val reader = jsonReader.peekJson()
        reader.beginObject()
        while (reader.hasNext()) {
            reader.nextName()
            reader.beginObject()
            while (reader.hasNext()) {
                if (reader.nextName() == "s") {
                    reader.beginObject()
                    while (reader.hasNext()) {
                        if (reader.nextName() == "u") {
                           images.add(reader.readJsonValue().toString())
                        }
                       if (reader.peek() !== JsonReader.Token.END_OBJECT) {
                           reader.skipValue()
                       }
                    }
                }
                if (reader.peek() !== JsonReader.Token.END_OBJECT) {
                    reader.skipValue()
                } else reader.endObject()
            }
            reader.endObject()
        }
        jsonReader.skipValue()
        return GalleryDto(
            images = images
        )
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