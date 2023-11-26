package com.fadhlalhafizh.storyapplication.data.local.di

import android.content.Context
import com.fadhlalhafizh.storyapplication.data.api.retrofit.ApiConfig
import com.fadhlalhafizh.storyapplication.data.local.database.localbase.StoryDatabase
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.fadhlalhafizh.storyapplication.data.pref.UserPreference
import com.fadhlalhafizh.storyapplication.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val storyDatabase = StoryDatabase.getStoryDatabase(context)
        return AuthRepository.getInstance(pref, apiService, storyDatabase)
    }
}