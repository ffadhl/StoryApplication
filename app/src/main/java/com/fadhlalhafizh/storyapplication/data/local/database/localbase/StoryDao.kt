package com.fadhlalhafizh.storyapplication.data.local.database.localbase

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fadhlalhafizh.storyapplication.data.api.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<ListStoryItem>)

    @Query("SELECT * FROM storyItem")
    fun getAllStoryItem(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM storyItem")
    suspend fun deleteAll()
}