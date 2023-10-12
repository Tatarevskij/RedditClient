package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.UserFull
import javax.inject.Inject

class GetUserFullByNameUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(name: String): UserFull? {
       return repository.getUserFullByName(name)
    }
}