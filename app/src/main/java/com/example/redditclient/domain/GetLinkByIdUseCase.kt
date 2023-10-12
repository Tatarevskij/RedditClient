package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Comment
import com.example.redditclient.entity.Link
import javax.inject.Inject

class GetLinkByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String, limit: Int): Pair<Link, List<Comment>>? {
        return repository.getLinkById(id, limit)
    }
}