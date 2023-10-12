package com.example.redditclient.domain

import com.example.redditclient.data.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetRepoStatusFlowUseCase @Inject constructor(
    private val repository: Repository
) {
    fun execute (): StateFlow<String?> {
        return repository.repoStatusFlow
    }
}