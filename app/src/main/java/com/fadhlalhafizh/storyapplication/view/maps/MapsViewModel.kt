package com.fadhlalhafizh.storyapplication.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fadhlalhafizh.storyapplication.data.api.response.GetAllStoryResponse
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.fadhlalhafizh.storyapplication.data.pref.UserModel

class MapsViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    suspend fun getLocation(token: String): GetAllStoryResponse {
        return authRepository.getLocation(token)
    }
}