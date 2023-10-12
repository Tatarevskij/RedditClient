package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import javax.inject.Inject

class SaveByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String, linkId: String) {
        repository.saveById(id, linkId)
    }
}