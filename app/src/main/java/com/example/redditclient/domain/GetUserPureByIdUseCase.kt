package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.UserPure
import javax.inject.Inject

class GetUserPureByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String): UserPure? {
        return repository.getUserPureById(id)
    }
}