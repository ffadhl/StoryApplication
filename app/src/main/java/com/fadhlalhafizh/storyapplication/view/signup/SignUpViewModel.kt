package com.fadhlalhafizh.storyapplication.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadhlalhafizh.storyapplication.data.api.response.ErrorResponse
import com.fadhlalhafizh.storyapplication.data.local.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isErrorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _isErrorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val message = authRepository.signupUser(name, email, password).message
                _isErrorMessage.value = message!!
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                _isErrorMessage.value = errorMessage!!
            } finally {
                _isLoading.value = false
            }
        }
    }
}