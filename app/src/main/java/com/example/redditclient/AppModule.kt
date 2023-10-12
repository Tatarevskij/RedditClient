package com.example.redditclient

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.redditclient.authorization.AppAuth
import com.example.redditclient.data.RedditApi
import com.example.redditclient.data.Repository
import com.example.redditclient.data.db.AppDatabase
import com.example.redditclient.data.db.CommentDao
import com.example.redditclient.data.db.LinkDao
import com.example.redditclient.data.db.RedditDb
import com.example.redditclient.entity.State
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val SHARED_PREFS_NAME = "saved_token"
const val ONBOARD_SCR = "onboard_screen"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationComponent (i.e. everywhere in the application)
    @Provides
    fun provideRedditDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "reddit_db"
    ).build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun provideLinkDao(db: AppDatabase): LinkDao =
        db.getLinkDao() // The reason we can implement a Dao for the database

    @Singleton
    @Provides
    fun provideCommentDao(db: AppDatabase): CommentDao =
        db.getCommentDao() // The reason we can implement a Dao for the database

    @Singleton
    @Provides
    fun sharedPrefsProvider(
        @ApplicationContext app: Context
    ): SharedPreferences = app.getSharedPreferences(
        SHARED_PREFS_NAME,
        Context.MODE_PRIVATE
    )

    @Singleton
    @Provides
    fun redditApiProvider(
        @ApplicationContext app: Context,
    ): RedditApi = RedditApi(app)

    @Singleton
    @Provides
    fun appAuthProvider(
    ): AppAuth = AppAuth

    @Singleton
    @Provides
    fun appStateProvider(
        @ApplicationContext app: Context
    ): State = State(app)

    @Singleton
    @Provides
    fun appOptionsProvider(
    ): Options = Options()

    @Singleton
    @Provides
    fun repositoryProvider(
        @ApplicationContext app: Context,
        sp: SharedPreferences, db: RedditDb, api: RedditApi, state: State,
    ): Repository = Repository(sp, db, api, app, state)
}