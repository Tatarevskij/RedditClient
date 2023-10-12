package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Link
import javax.inject.Inject

class GetLinksByIdsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(ids: List<String>): List<Link>? {
        return repository.getLinksByIds(ids)
    }
}