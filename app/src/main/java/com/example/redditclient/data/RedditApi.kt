package com.example.redditclient.data

import android.content.Context
import android.util.Log
import com.example.redditclient.authorization.AuthorizationFailedInterceptor
import com.example.redditclient.authorization.AuthorizationInterceptor
import com.example.redditclient.authorization.TokenStorage
import com.example.redditclient.data.adapters.*
import com.example.redditclient.data.dto.*
import com.example.redditclient.entity.*
import com.squareup.moshi.Moshi
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

private const val BASE_URL = "https://oauth.reddit.com"

class RedditApi @Inject constructor(
    context: Context
){
    private val moshiCustom: Moshi = Moshi.Builder()
        .add(RepliesAdapter())
      //  .add(DataCustomAdapter())
        .add(GalleryAdapter())
        .add(UserPureListAdapter())
        .add(UserPureAdapter())
        .add(AnyDataAdapter())
        .build()

    private val okhttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(
            HttpLoggingInterceptor {
              //  Log.d("Network", it)
            }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .addNetworkInterceptor(AuthorizationInterceptor())
        .addNetworkInterceptor(AuthorizationFailedInterceptor(AuthorizationService(context), TokenStorage))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okhttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshiCustom))
        .build()



    private val linksApi: LinksApi = retrofit.create(
        LinksApi::class.java
    )

    private val commentsApi: CommentsApi = retrofit.create(
        CommentsApi::class.java
    )

    private val userPureApi: UserPureApi = retrofit.create(
        UserPureApi::class.java
    )

    private val userFullApi: UserFullApi = retrofit.create(
        UserFullApi::class.java
    )

    private val subredditApi: SubredditApi = retrofit.create(
        SubredditApi::class.java
    )

    private val meApi: MeApi = retrofit.create(
        MeApi::class.java
    )

    private val controlsApi: ControlsApi = retrofit.create(
        ControlsApi::class.java
    )

    suspend fun getBestLinks(after: String): LinkListing {
        return this.linksApi.getBestLinksFromDto(after, 1, 1)
    }

    suspend fun getNewLinks(after: String): LinkListing {
        return this.linksApi.getNewLinksFromDto(after, 1, 1)
    }

    suspend fun getLinkById(id: String, limit: Int): List<AnyDataSealed> {
        return this.linksApi.getLinkByIdFromDto(id, 1, limit,  4, 1)
    }

    suspend fun getLinksByQuery(query: String, after: String): LinkListing {
        return this.linksApi.getLinksByQueryFromDto(query, after,1, 1)
    }

    suspend fun getLinksBySrName(name: String, after: String): LinkListing {
        return this.linksApi.getNewLinksBySrNameFromDto(name, after)
    }

    suspend fun getLinksByIds(ids: String): LinkListing {
        return this.linksApi.getLinksByIdsFromDto(ids)
    }

    suspend fun getChildrenCommentsByLinkAndParentId(linkId: String, commentId: String): List<AnyDataSealed> {
        return this.commentsApi.getChildrenCommentsByLinkAndParentIdFromDto(linkId, commentId, 0,0,0,1)
    }

    suspend fun getMoreChildren(linkId: String, children: String): List<Comment> {
        return this.commentsApi.getMoreChildrenFromDto(linkId, children, 1, 4).json.data.things
    }

    suspend fun getUserPureById(id: String): UserPure{
        return this.userPureApi.getUserPureByIdFromDto(id, 1)
    }

    suspend fun getUsersPureById(id: String): List<UserPure>{
        return this.userPureApi.getUsersPureByIdFromDto(id, 1)
    }

    suspend fun getUserFullByName(name: String): AnyDataSealed.UserFull {
        return this.userFullApi.getUserFullFromDto(name, 1) as AnyDataSealed.UserFull
    }

    suspend fun getUserOverview(name: String): AnyDataSealed.Listing {
        return this.userFullApi.getUsersOverview(name) as AnyDataSealed.Listing
    }

    suspend fun getUserSavedData(name: String): AnyDataSealed.Listing {
        return this.userFullApi.getUserSavedData(name) as AnyDataSealed.Listing
    }

    suspend fun getSubredditDetails(name: String): AnyDataSealed.Subreddit {
        return this.subredditApi.getSubredditDetails(name) as AnyDataSealed.Subreddit
    }

    suspend fun getMyInfo(): Me {
        return this.meApi.getMyInfoFromDto()
    }

    suspend fun getMyFriends(): AnyDataSealed.UserList{
        return this.meApi.getMyFriendsFromDto() as AnyDataSealed.UserList
    }

    suspend fun saveById(id: String) {
        this.controlsApi.saveById(id)
    }

    suspend fun unSaveById(id: String) {
        this.controlsApi.unSaveById(id)
    }
}

interface LinksApi {
    @GET("best?")
    suspend fun getBestLinksFromDto(
        @Query("after") after: String,
        @Query("sr_detail") srDetails: Int,
        @Query("raw_json") rawJson: Int
    ): LinkListingDto

    @GET("new?")
    suspend fun getNewLinksFromDto(
        @Query("after") after: String,
        @Query("sr_detail") srDetails: Int,
        @Query("raw_json") rawJson: Int
    ): LinkListingDto

    @GET("search?")
    suspend fun getLinksByQueryFromDto(
        @Query("q") query: String,
        @Query("after") after: String,
        @Query("sr_detail") srDetails: Int,
        @Query("raw_json") rawJson: Int
    ): LinkListingDto

    @GET("comments/{id}?")
    suspend fun getLinkByIdFromDto(
        @Path("id") id: String,
        @Query("sr_detail") srDetails: Int,
        @Query("limit") limit: Int,
        @Query("depth") depth: Int,
        @Query("raw_json") rawJson: Int
    ): List<AnyDataSealed>

    @GET("r/{name}?raw_json=1")
    suspend fun getNewLinksBySrNameFromDto(
        @Path("name") name: String,
        @Query("after") after: String,
    ): LinkListingDto

    @GET("by_id/{ids}?raw_json=1")
    suspend fun getLinksByIdsFromDto(
        @Path("ids") ids: String,
    ): LinkListingDto
}

interface CommentsApi {
    @GET("comments/{linkId}?")
    suspend fun getChildrenCommentsByLinkAndParentIdFromDto(
        @Path("linkId") linkId: String,
        @Query("comment") commentId: String,
        @Query("depth") depth: Int,
        @Query("limit") limit: Int,
        @Query("context") context: Int,
        @Query("raw_json") rawJson: Int
    ): List<AnyDataSealed>

    @GET("/api/morechildren?")
    suspend fun getMoreChildrenFromDto(
        @Query("link_id") linkId: String,
        @Query("children") children: String,
        @Query("raw_json") rawJson: Int,
        @Query("depth") depth: Int,
        @Query("api_type") apiType: String = "json"
    ): MoreChildrenDto
}

interface UserPureApi {
    @GET("/api/user_data_by_account_ids?")
    suspend fun getUserPureByIdFromDto(
        @Query("ids") id: String,
        @Query("raw_json") rawJson: Int
    ): UserPureDto

    @GET("/api/user_data_by_account_ids?")
    suspend fun getUsersPureByIdFromDto(
        @Query("ids") id: String,
        @Query("raw_json") rawJson: Int
    ): List<UserPureDto>
}

interface UserFullApi {
    @GET("/user/username/about?")
    suspend fun getUserFullFromDto(
        @Query("username") username: String,
        @Query("raw_json") rawJson: Int
    ): AnyDataSealed

    @GET("/user/{name}/overview?raw_json=1")
    suspend fun getUsersOverview(
        @Path("name") name: String,
    ): AnyDataSealed

    @GET("/user/{name}/saved?raw_json=1")
    suspend fun getUserSavedData(
        @Path("name") name: String,
    ): AnyDataSealed
}

interface SubredditApi {
    @GET("/r/{name}/about?raw_json=1")
    suspend fun getSubredditDetails(
        @Path("name") name: String,
    ): AnyDataSealed
}

interface MeApi {
    @GET("/api/v1/me?raw_json=1")
    suspend fun getMyInfoFromDto(): MeDto

    @GET("api/v1/me/friends")
    suspend fun getMyFriendsFromDto(): AnyDataSealed
}

interface ControlsApi {
    @POST("/api/save?")
    suspend fun saveById(
        @Query("id") id: String,
    )

    @POST("/api/unsave?")
    suspend fun unSaveById(
        @Query("id") id: String,
    )
}