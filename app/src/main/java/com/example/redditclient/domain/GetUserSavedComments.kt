package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Comment
import javax.inject.Inject

class GetUserSavedComments @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(name: String): List<Comment>? {
        return repository.getUserSavedComments(name)
    }
}