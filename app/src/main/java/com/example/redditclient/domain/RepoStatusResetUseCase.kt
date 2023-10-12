package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import javax.inject.Inject

class RepoStatusResetUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute() {
        repository.statusReset()
    }
}