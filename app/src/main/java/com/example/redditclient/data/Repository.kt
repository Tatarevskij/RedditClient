package com.example.redditclient.data


import android.content.Context
import android.content.SharedPreferences
import com.example.redditclient.R
import com.example.redditclient.data.adapters.AnyDataSealed
import com.example.redditclient.data.db.RedditDb
import com.example.redditclient.data.dto.*
import com.example.redditclient.entity.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class Repository @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val redditDb: RedditDb,
    private var redditApi: RedditApi,
    private val context: Context,
    private val state: State
) {
   // private var redditApi = RedditApi(sharedPreferences.getString(ACCESS_TOKEN, ""))
    private val _repoStatusFlow = MutableStateFlow<String?>(null)
    val repoStatusFlow = _repoStatusFlow.asStateFlow()

    suspend fun getBestLinks(after: String): LinkListing? {
        return try {
            getBestLinksFromApi(after)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getNewLinks(after: String): LinkListing? {
        return try {
            getNewLinksFromApi(after)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getLinksByQuery(query: String, after: String): LinkListing? {
        return try {
            getLinksByQueryFromApi(query, after)
        } catch (ex: Exception) {
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getLinkById(id: String, limit: Int): Pair<Link, List<Comment>>? {
        return try {
            getLinkByIdFromApi(id, limit)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getLinksBySrName(name: String, after: String): LinkListing? {
        return try {
            getLinksBySrNameFromApi(name, after)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getLinksByIds(ids: List<String>): List<Link>? {
        return try {
            getLinksByIdsFromApi(ids)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getChildrenComments(linkId: String, commentId: String): List<Comment>? {
        return try {
            getChildrenCommentsFromApi(linkId, commentId)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getMoreChildren(linkId: String, children: List<String>): List<Comment>? {
        return try {
            getMoreChildrenFromApi(linkId, children)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }


    suspend fun getUserPureById(id: String): UserPure? {
        return try {
            getUserPureByIdFromApi(id)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getUserFullByName(name: String): UserFull? {
        return try {
            getUserFullByIdFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getUserOverviewLinks(name: String): List<Link>? {
        return try {
            getUserOverviewLinksFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getUserOverviewComments(name: String): List<Comment>? {
        return try {
            getUserOverviewCommentsFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getUserSavedLinks(name: String): List<Link>? {
        return try {
            getUserSavedLinksFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message + context.getString(R.string.load_from_cache))
            getAllLinksFromDb()
        }
    }

    suspend fun getUserSavedComments(name: String): List<Comment>? {
        return try {
            getUserSavedCommentsFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message + context.getString(R.string.load_from_cache))
            getAllCommentsFromDb()
        }
    }

    suspend fun getSubredditDetails(name: String): Subreddit? {
        return try {
            getSubredditDetailsFromApi(name)
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }


    suspend fun getMyInfo(): Me? {
        return try {
            getMyInfoFromApi()
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }

    suspend fun getMyFriends(): List<UserPure>? {
        val users: MutableList<String> = mutableListOf()
        return try {
            getFriendsFromApi().data?.children?.forEach {
                users.add(it.id)
            }
            getUsersPureByIdFromApi(users.joinToString(","))
        } catch (ex: Exception) {
            println(ex)
            state.isOnLineCheck()
            _repoStatusFlow.emit( context.getString(R.string.server_failed) + ex.message)
            null
        }
    }


    private suspend fun getBestLinksFromApi(after: String): LinkListing {
        return withTimeout(5000L) {
            redditApi.getBestLinks(after)
        }
    }

    private suspend fun getNewLinksFromApi(after: String): LinkListing {
        return withTimeout(5000L) {
            redditApi.getNewLinks(after)
        }
    }

    private suspend fun getLinksByQueryFromApi(query: String, after: String): LinkListing {
        return withTimeout(5000L) {
            redditApi.getLinksByQuery(query, after)
        }
    }

    private suspend fun getLinkByIdFromApi(id: String, limit: Int): Pair<Link, List<Comment>> {
        val linkWithComments: Pair<Link, List<Comment>>
        withTimeout(5000L) {
            linkWithComments = LinkWithCommentDto(redditApi.getLinkById(id, limit)).execute()
        }
        return linkWithComments
    }

    private suspend fun getLinksBySrNameFromApi(name: String, after: String): LinkListing {
        return withTimeout(5000L) {
            redditApi.getLinksBySrName(name, after)
        }
    }

    private suspend fun getLinksByIdsFromApi(ids: List<String>): List<Link> {
        val idsString = ids.joinToString(",")
        return withTimeout(5000L) {
            redditApi.getLinksByIds(idsString).data.children
        }
    }

    private suspend fun getChildrenCommentsFromApi(
        linkId: String,
        commentId: String
    ): List<Comment> {
        val linkWithComments: List<Comment>
        withTimeout(5000L) {
            linkWithComments = CommentsWithoutLinkDto(
                redditApi.getChildrenCommentsByLinkAndParentId(
                    linkId,
                    commentId
                )
            ).execute()
        }
        return linkWithComments
    }

    private suspend fun getMoreChildrenFromApi(
        linkId: String,
        children: List<String>
    ): List<Comment> {
        return withTimeout(5000L) {
            redditApi.getMoreChildren(linkId, children.joinToString(separator = ","))
        }
    }

    private suspend fun getUserPureByIdFromApi(id: String): UserPure {
        return withTimeout(5000L) {
            redditApi.getUserPureById(id)
        }
    }

    private suspend fun getUsersPureByIdFromApi(id: String): List<UserPure> {
        return withTimeout(5000L) {
            redditApi.getUsersPureById(id)
        }
    }

    private suspend fun getUserFullByIdFromApi(name: String): UserFullDto {
        return withTimeout(5000L) {
            redditApi.getUserFullByName(name).data
        }
    }

    private suspend fun getUserOverviewLinksFromApi(name: String): List<Link> {
        val overview: MutableList<Link> = mutableListOf()
        withTimeout(5000L) {
            redditApi.getUserOverview(name).data?.children?.forEach {
                println(it)
                when (it) {
                    is AnyDataSealed.Link -> overview.add(SealedLinkDto(it.data) as Link)
                    else -> return@forEach
                }
            }
        }
        return overview
    }

    private suspend fun getUserOverviewCommentsFromApi(name: String): List<Comment> {
        val overview: MutableList<Comment> = mutableListOf()
        withTimeout(5000L) {
            redditApi.getUserOverview(name).data?.children?.forEach {
                when (it) {
                    is AnyDataSealed.Comment -> overview.add(
                        SealedCommentDto(
                            kind = it.kind,
                            data = it.data
                        ) as Comment
                    )
                    else -> return@forEach
                }
            }
        }
        return overview
    }

    private suspend fun getUserSavedLinksFromApi(name: String): List<Link> {
        val links: MutableList<Link> = mutableListOf()
        withTimeout(5000L) {
            redditApi.getUserSavedData(name).data?.children?.forEach {
                when (it) {
                    is AnyDataSealed.Link -> links.add(SealedLinkDto(it.data) as Link)
                    else -> return@forEach
                }
            }
        }
        return links
    }

    private suspend fun getUserSavedCommentsFromApi(name: String): List<Comment> {
        val comments: MutableList<Comment> = mutableListOf()
        withTimeout(5000L) {
            redditApi.getUserSavedData(name).data?.children?.forEach {
                when (it) {
                    is AnyDataSealed.Comment -> comments.add(
                        SealedCommentDto(
                            kind = it.kind,
                            data = it.data
                        ) as Comment
                    )
                    else -> return@forEach
                }
            }
        }
        return comments
    }

    private suspend fun getSubredditDetailsFromApi(name: String): Subreddit {
        return redditApi.getSubredditDetails(name).data
    }

    private suspend fun getFriendsFromApi(): AnyDataSealed.UserList {
        return redditApi.getMyFriends()
    }

    private suspend fun getMyInfoFromApi(): Me {
        return redditApi.getMyInfo()
    }

    suspend fun saveById(id: String, linkId: String) {
        try {
            redditApi.saveById(id)
        } catch (ex: Exception) {
            println(ex)
        }

        when {
            id.contains("t3") -> addLinkToDb(id)
            id.contains("t1") -> addCommentToDb(id, linkId)
        }
    }

    suspend fun unSaveById(id: String) {
        try {
            redditApi.unSaveById(id)
        } catch (ex: Exception) {
            println(ex)
        }

        when {
            id.contains("t3") -> deleteLinkFromDb(id)
            id.contains("t1") -> deleteCommentFromDb(id)
        }
    }

    private suspend fun addLinkToDb(id: String) {
        val link = getLinkById(id.substringAfter("t3_"), 1)
        println(link)
        link?.first?.let { LinkToLinkDbDto(it).execute() }?.let { redditDb.addLink(it) }
    }

    private suspend fun deleteLinkFromDb(name: String) {
        redditDb.deleteLink(name)
    }

    private suspend fun getAllLinksFromDb(): List<Link> {
        state.isOnLineCheck()
        val links = mutableListOf<Link>()
        redditDb.getAllLinks().forEach {
            links.add(LinkDbToLinkDto(it))
        }
        return links
    }

    private suspend fun addCommentToDb(id: String, linkId: String) {
        val comment: Comment
        try {
            comment = redditApi.getMoreChildren(linkId, id.substringAfter("t1_"))[0]
            redditDb.addComment(CommentToCommentDbDto(comment).execute())
        } catch (ex: Exception) {
            println(ex)
        }
    }

    private suspend fun deleteCommentFromDb(id: String) {
        redditDb.deleteComment(id)
    }

    private suspend fun getAllCommentsFromDb(): List<Comment> {
        state.isOnLineCheck()
        val comments = mutableListOf<Comment>()
        redditDb.getAllComments().forEach {
            comments.add(CommentDbToCommentDto(it))
        }
        return comments
    }

    suspend fun clearDb() {
        redditDb.clear()
    }

    fun apiRefresh() {
       // redditApi = RedditApi(sharedPreferences.getString(ACCESS_TOKEN, ""))
    }

    suspend fun statusReset() {
        _repoStatusFlow.emit(null)
    }

}