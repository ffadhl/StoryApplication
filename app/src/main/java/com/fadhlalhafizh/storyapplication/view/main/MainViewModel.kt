package com.fadhlalhafizh.storyapplication.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fadhlalhafizh.storyapplication.data.api.response.ListStoryItem
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.fadhlalhafizh.storyapplication.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    val getStoriesWithPager: LiveData<PagingData<ListStoryItem>> by lazy {
        authRepository.getStoriesWithPager().cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}