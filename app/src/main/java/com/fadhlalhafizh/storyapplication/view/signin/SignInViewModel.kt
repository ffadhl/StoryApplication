package com.fadhlalhafizh.storyapplication.view.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fadhlalhafizh.storyapplication.data.api.response.SignInResponse
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.fadhlalhafizh.storyapplication.data.pref.UserModel
import kotlinx.coroutines.launch

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            authRepository.saveSession(userModel)
        }
    }

    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }

    suspend fun signIn(email: String, password: String): SignInResponse {
        _isLoading.postValue(true)
        val response = authRepository.signInUser(email, password)
        _isLoading.postValue(false)
        return response
    }
}