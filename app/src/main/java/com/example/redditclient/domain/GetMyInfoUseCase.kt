package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Me
import javax.inject.Inject

class GetMyInfoUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(): Me? {
        return repository.getMyInfo()
    }
}