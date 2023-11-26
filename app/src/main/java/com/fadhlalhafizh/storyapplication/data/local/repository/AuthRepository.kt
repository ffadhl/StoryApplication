package com.fadhlalhafizh.storyapplication.data.local.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fadhlalhafizh.storyapplication.data.StoryPagingSource
import com.fadhlalhafizh.storyapplication.data.api.response.AddStoryResponse
import com.fadhlalhafizh.storyapplication.data.api.response.GetAllStoryResponse
import com.fadhlalhafizh.storyapplication.data.api.response.ListStoryItem
import com.fadhlalhafizh.storyapplication.data.api.response.SignInResponse
import com.fadhlalhafizh.storyapplication.data.api.response.SignUpResponse
import com.fadhlalhafizh.storyapplication.data.api.retrofit.ApiService
import com.fadhlalhafizh.storyapplication.data.local.database.StoryRemoteMediator
import com.fadhlalhafizh.storyapplication.data.local.database.localbase.StoryDatabase
import com.fadhlalhafizh.storyapplication.data.pref.UserModel
import com.fadhlalhafizh.storyapplication.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getUserSession()
    }

    suspend fun logout() {
        userPreference.clearToken()
    }

    suspend fun signupUser(name: String, email: String, password: String): SignUpResponse {
        return apiService.signup(name, email, password)
    }

    suspend fun signInUser(email: String, password: String): SignInResponse {
        return apiService.signin(email, password)
    }

    suspend fun addStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        return apiService.addStoryPhotos(token = "Bearer $token", file, description)
    }

    suspend fun getLocation(token: String): GetAllStoryResponse {
        return apiService.getLocation(token = "Bearer $token")
    }

    fun getStoriesWithPager(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory = {
                StoryPagingSource(apiService, userPreference)
                storyDatabase.storyDao().getAllStoryItem()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(userPreference, apiService, storyDatabase)
            }.also { instance = it }
    }
}