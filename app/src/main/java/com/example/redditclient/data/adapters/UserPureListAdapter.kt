package com.example.redditclient.data.adapters

import com.example.redditclient.data.dto.UserPureDto
import com.example.redditclient.entity.UserPure
import com.squareup.moshi.*

class UserPureListAdapter {
    @FromJson
    fun fromJson(
        jsonReader: JsonReader
    ): List<UserPureDto> {

        val users: MutableList<UserPureDto> = mutableListOf()
        jsonReader.beginObject()
        val nameReader = jsonReader.peekJson()
        jsonReader.nextName()

        while (jsonReader.hasNext()) {
            var name: String? = null
            var imgSrc: String? = null
            val userPure: UserPure
            val reader = jsonReader.peekJson()
            val id: String = nameReader.nextName()

            nameReader.skipValue()
            reader.beginObject()
            while (reader.peek() == JsonReader.Token.NAME){
                when(reader.nextName()) {
                    "name" -> name = reader.readJsonValue().toString()
                    "profile_img" -> imgSrc = reader.readJsonValue().toString()
                    else -> reader.skipValue()
                }

            }
            reader.endObject()
            jsonReader.skipValue()
            if (jsonReader.peek() == JsonReader.Token.END_OBJECT) jsonReader.endObject()
            else jsonReader.skipValue()

            userPure = UserPureDto(
                id = id,
                name = name!!,
                profileImg = imgSrc!!
            )
            users.add(userPure)
        }
        return users
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