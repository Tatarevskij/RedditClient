package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Link
import javax.inject.Inject

class GetUserOverviewLinksUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(name: String): List<Link>? {
       return repository.getUserOverviewLinks(name)
    }
}