package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Subreddit
import javax.inject.Inject

class GetSubredditDetailsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(name: String): Subreddit? {
        return repository.getSubredditDetails(name)
    }
}