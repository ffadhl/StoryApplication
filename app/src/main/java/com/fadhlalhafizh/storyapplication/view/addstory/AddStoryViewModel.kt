package com.fadhlalhafizh.storyapplication.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fadhlalhafizh.storyapplication.data.api.response.AddStoryResponse
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.fadhlalhafizh.storyapplication.data.pref.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    suspend fun upload(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): AddStoryResponse {
        return authRepository.addStories(token, file, description)
    }

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
}
