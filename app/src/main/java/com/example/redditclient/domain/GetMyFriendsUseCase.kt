package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import com.example.redditclient.entity.UserPure
import javax.inject.Inject

class GetMyFriendsUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(): List<UserPure>? {
        return repository.getMyFriends()
    }
}