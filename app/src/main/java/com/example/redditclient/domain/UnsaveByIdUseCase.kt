package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import javax.inject.Inject

class UnSaveByIdUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend fun execute(id: String) {
        repository.unSaveById(id)
    }
}