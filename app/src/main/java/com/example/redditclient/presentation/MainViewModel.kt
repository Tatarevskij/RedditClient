package com.example.redditclient.presentation

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.redditclient.ACCESS_TOKEN
import com.example.redditclient.Options
import com.example.redditclient.REFRESH_TOKEN
import com.example.redditclient.data.pagingSource.BestLinksPagingSource
import com.example.redditclient.data.pagingSource.LinksByQueryPagingSource
import com.example.redditclient.data.pagingSource.LinksBySrNamePagingSource
import com.example.redditclient.data.pagingSource.NewLinksPagingSource
import com.example.redditclient.domain.*
import com.example.redditclient.entity.*
import com.example.redditclient.presentation.adapters.LinksPagedAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getLinkByIdUseCase: GetLinkByIdUseCase,
    private val getUserPureByIdUseCase: GetUserPureByIdUseCase,
    private val getChildrenCommentsUseCase: GetChildrenCommentsUseCase,
    private val getMoreChildrenUseCase: GetMoreChildrenUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getMyFriendsUseCase: GetMyFriendsUseCase,
    private val getUserFullByNameUseCase: GetUserFullByNameUseCase,
    private val getUserOverviewLinksUseCase: GetUserOverviewLinksUseCase,
    private val getUserOverviewCommentsUseCase: GetUserOverviewCommentsUseCase,
    private val getSubredditDetailsUseCase: GetSubredditDetailsUseCase,
    private val getLinksByIdsUseCase: GetLinksByIdsUseCase,
    private val getUserSavedLinks: GetUserSavedLinks,
    private val getUserSavedComments: GetUserSavedComments,
    private val saveByIdUseCase: SaveByIdUseCase,
    private val unSaveByIdUseCase: UnSaveByIdUseCase,
    private val clearDbUseCase: ClearDbUseCase,
    private val getRepoStatusFlowUseCase: GetRepoStatusFlowUseCase,
    private val apiRefreshUseCase: ApiRefreshUseCase,
    private val repoStatusResetUseCase: RepoStatusResetUseCase,
    private val tokenPrefs: SharedPreferences,
    val options: Options,
    val state: State,
    application: Application
) : AndroidViewModel(application) {
    private var userName: String = ""
    private val linksHistory: MutableList<String> = mutableListOf()
    private val _linkByIdFlow = MutableStateFlow<Pair<Link, List<Comment>>?>(null)
    val linkByIdFlow = _linkByIdFlow.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _friendsFlow = MutableStateFlow<List<UserPure>?>(null)
    val friendsFlow = _friendsFlow.asStateFlow()

    private val _linksFlow = MutableStateFlow<List<Link>?>(null)
    val linksFlow = _linksFlow.asStateFlow()

    private val _commentsFlow = MutableStateFlow<List<Comment>?>(null)
    val commentsFlow = _commentsFlow.asStateFlow()

    private val _userFlow = MutableStateFlow<UserFull?>(null)
    val userFlow = _userFlow.asStateFlow()

    private val _subredditFlow = MutableStateFlow<Subreddit?>(null)
    val subredditFlow = _subredditFlow.asStateFlow()

    private val _appStatusFlow = MutableStateFlow<String?>(null)
    private val appStatusFlow = _appStatusFlow.asStateFlow()

    val statusFlow: StateFlow<AppState> = combine(getRepoStatusFlowUseCase.execute(), appStatusFlow) { repo, app ->
        AppState(repo, app)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = AppState())

    data class AppState(
        val repoStatus: String? = null,
        val appStatus: String? = null
    )

    init {
        viewModelScope.launch {
            state.onLineStatusFlow.collect {
                Log.d("network_status","$it")
                if (it) refreshApi()
            }
        }
    }

    private fun loadNewLinksPagingFlow(): Flow<PagingData<Link>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = { NewLinksPagingSource(getRepositoryUseCase.execute()) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadBestLinksPagingFlow(): Flow<PagingData<Link>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = { BestLinksPagingSource(getRepositoryUseCase.execute()) }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadLinksBySrNamePagingFlow(name: String): Flow<PagingData<Link>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = {
                LinksBySrNamePagingSource(
                    getRepositoryUseCase.execute(),
                    name
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    private fun loadLinksByQueryPagingFlow(query: String): Flow<PagingData<Link>> {
        return Pager(
            config = PagingConfig(1),
            pagingSourceFactory = {
                LinksByQueryPagingSource(
                    getRepositoryUseCase.execute(),
                    query
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    fun loadNewLinks(linksPagedAdapter: LinksPagedAdapter) {
        viewModelScope.launch {
            linksPagedAdapter.loadStateFlow.collect {
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error) {
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadNewLinksPagingFlow().collect {
                linksPagedAdapter.submitData(it)
            }
            linksPagedAdapter.refresh()
        }
    }

    fun loadBestLinks(linksPagedAdapter: LinksPagedAdapter) {
        viewModelScope.launch {
            linksPagedAdapter.loadStateFlow.collect {
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error) {
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadBestLinksPagingFlow().collect {
                linksPagedAdapter.submitData(it)
            }
            linksPagedAdapter.refresh()
        }
    }

    fun loadLinksByQuery(linksPagedAdapter: LinksPagedAdapter, query: String) {
        viewModelScope.launch {
            linksPagedAdapter.loadStateFlow.collect {
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error) {
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadLinksByQueryPagingFlow(query).collect {
                linksPagedAdapter.submitData(it)
            }
            linksPagedAdapter.refresh()
        }
    }

    fun loadLinksBySrName(linksPagedAdapter: LinksPagedAdapter, name: String) {
        viewModelScope.launch {
            linksPagedAdapter.loadStateFlow.collect {
                _isLoading.emit(it.source.refresh is LoadState.Loading)
                if (it.source.refresh is LoadState.Error) {
                    _isLoading.value = false
                    _appStatusFlow.emit((it.source.refresh as LoadState.Error).error.message)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            loadLinksBySrNamePagingFlow(name).collect {
                linksPagedAdapter.submitData(it)
            }
        }
    }


    fun loadLinkById(id: String, limit: Int = 0) {
        linksHistory.add(id)
        viewModelScope.launch {
            _linkByIdFlow.emit(null)
            _linkByIdFlow.emit(getLinkByIdUseCase.execute(id.substringAfter("t3_"), limit))
        }
    }

    private fun loadLinksByIds(ids: List<String>) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
               getLinksByIdsUseCase.execute(ids)
            }.fold(
                onSuccess = {
                    _linksFlow.value = it
                    _linksFlow.value = null
                },
                onFailure = {
                    _appStatusFlow.emit(it.message)
                    _linksFlow.value = null}
            )
            _isLoading.value = false
        }
    }

    fun loadLinksFromHistory() {
        loadLinksByIds(linksHistory)
    }

    fun loadSubredditDetails(name: String) {
        viewModelScope.launch {
            _subredditFlow.emit(null)
            _subredditFlow.emit(getSubredditDetailsUseCase.execute(name))
        }
    }

    suspend fun loadMyInfo(): Me? {
        return getMyInfoUseCase.execute()
    }

    suspend fun getUserPureById(id: String): UserPure? {
        return getUserPureByIdUseCase.execute(id)
    }

    suspend fun getChildrenComments(linkId: String, commentId: String): List<Comment>? {
        return getChildrenCommentsUseCase.execute(linkId, commentId)
    }

    suspend fun getMoreChildren(linkId: String, children: List<String>): List<Comment>? {
        return getMoreChildrenUseCase.execute(linkId, children)
    }

    fun loadMyFriends() {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getMyFriendsUseCase.execute()
            }.fold(
                onSuccess = { _friendsFlow.value = it },
                onFailure = { _appStatusFlow.emit(it.message) }
            )
            _isLoading.value = false
        }
    }

    fun loadUserFull(name: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getUserFullByNameUseCase.execute(name)
            }.fold(
                onSuccess = {
                    _userFlow.value = it
                    _userFlow.value = null
                },
                onFailure = { _appStatusFlow.emit(it.message) }
            )
            _isLoading.value = false
        }
    }

    fun loadUserLinks(name: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getUserOverviewLinksUseCase.execute(name)
            }.fold(
                onSuccess = {
                    _linksFlow.value = null
                    _linksFlow.value = it

                },
                onFailure = {
                    _appStatusFlow.emit(it.message)
                    _linksFlow.value = null}
            )
            _isLoading.value = false
        }
    }

    fun loadUserComments(name: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getUserOverviewCommentsUseCase.execute(name)
            }.fold(
                onSuccess = {
                    _commentsFlow.value = null
                    _commentsFlow.value = it

                },
                onFailure = {
                    _appStatusFlow.emit(it.message)
                    _commentsFlow.value = null}
            )
            _isLoading.value = false
        }
    }

    private fun loadUserSavedLinks(name: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getUserSavedLinks.execute(name)
            }.fold(
                onSuccess = {
                    _linksFlow.value = null
                    _linksFlow.value = it
                },
                onFailure = {
                    _appStatusFlow.emit(it.message)
                    _linksFlow.value = null}
            )
            _isLoading.value = false
        }
    }

    private fun loadUserSavedComments(name: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                _isLoading.value = true
                getUserSavedComments.execute(name)
            }.fold(
                onSuccess = {
                    _commentsFlow.value = null
                    _commentsFlow.value = it

                },
                onFailure = {
                    _appStatusFlow.emit(it.message)
                    _commentsFlow.value = null}
            )
            _isLoading.value = false
        }
    }

    fun loadMySavedLinks() {
        loadUserSavedLinks(userName)
    }

    fun loadMySavedComments() {
        loadUserSavedComments(userName)
    }

    fun loadMyComments() {
        loadUserComments(userName)
    }

    fun loadMyLinks() {
        loadUserLinks(userName)
    }

    fun saveById(id: String, linkId: String = "") {
        println(id)
        viewModelScope.launch {
            saveByIdUseCase.execute(id, linkId)
        }
    }

    fun unSaveById(id: String) {
        viewModelScope.launch {
            unSaveByIdUseCase.execute(id)
        }
    }

    fun clearDb() {
        viewModelScope.launch {
            clearDbUseCase.execute()
        }
    }

    fun getToken(): String? {
        println(tokenPrefs.getString(ACCESS_TOKEN, null))
        println(tokenPrefs.getString(REFRESH_TOKEN, null))
        return tokenPrefs.getString(ACCESS_TOKEN, null)
    }

    fun setUserName() {
        viewModelScope.launch {
            userName = getMyInfoUseCase.execute()?.name.toString()
        }
    }

    fun refreshApi() {
        apiRefreshUseCase.execute()
    }

    suspend fun statusReset() {
        _appStatusFlow.emit(null)
        repoStatusResetUseCase.execute()
    }

    fun removeToken() {
        tokenPrefs.edit().remove(ACCESS_TOKEN).apply()
    }
}