package com.example.redditclient.data.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.redditclient.data.Repository
import com.example.redditclient.entity.Link
import com.example.redditclient.entity.LinkListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NewLinksPagingSource @Inject constructor(
    private val repository: Repository,
    private var after: String? = ""
) : PagingSource<Int, Link>() {

    private companion object {
        private const val FIRST_PAGE_NUMBER = 0
    }

    override fun getRefreshKey(state: PagingState<Int, Link>): Int = FIRST_PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Link> {
        val page = params.key ?: FIRST_PAGE_NUMBER
        if (after.isNullOrBlank()) after = "start"
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                repository.getNewLinks(after!!) as LinkListing
            }
        }.fold(
            onSuccess = {
                after = it.data.after
                LoadResult.Page(
                    data = it.data.children,
                    prevKey = null,
                    nextKey = if (it.data.after.isNullOrBlank()) null else page + 1
                )

            },
            onFailure = {
                println(it.message)
                LoadResult.Error(it)
            }
        )
    }
}