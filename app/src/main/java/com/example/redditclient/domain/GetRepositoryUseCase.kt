package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: Repository
) {
    fun execute(): Repository {
        return repository
    }
}