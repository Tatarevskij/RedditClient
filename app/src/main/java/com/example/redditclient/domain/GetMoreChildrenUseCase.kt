package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Comment
import javax.inject.Inject

class GetMoreChildrenUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(linkId: String, children: List<String>): List<Comment>? {
        return repository.getMoreChildren(linkId, children)
    }
}