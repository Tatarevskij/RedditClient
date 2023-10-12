package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Comment
import javax.inject.Inject

class GetChildrenCommentsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(linkId: String, commentId: String): List<Comment>? {
        return repository.getChildrenComments(linkId, commentId)
    }
}