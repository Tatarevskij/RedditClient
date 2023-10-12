package com.example.redditclient.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.redditclient.entity.LinkInDb
import com.example.redditclient.entity.NewLinkInDb

@Dao
interface LinkDao {
    @Transaction
    @Query("SELECT * FROM links")
    suspend fun getAllLinks(): List<LinkInDb>

    @Query("DELETE FROM links WHERE name = :name")
    suspend fun deleteLink(name: String)

    @Insert(entity = LinkInDb::class)
    suspend fun insertLink (link: NewLinkInDb)

    @Query("DELETE FROM links")
    suspend fun deleteAllLinks()
}