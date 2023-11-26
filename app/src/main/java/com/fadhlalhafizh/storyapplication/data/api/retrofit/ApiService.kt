package com.fadhlalhafizh.storyapplication.data.api.retrofit

import com.fadhlalhafizh.storyapplication.data.api.response.AddStoryResponse
import com.fadhlalhafizh.storyapplication.data.api.response.GetAllStoryResponse
import com.fadhlalhafizh.storyapplication.data.api.response.SignInResponse
import com.fadhlalhafizh.storyapplication.data.api.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun signup(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun signin(
        @Field("email") email: String,
        @Field("password") password: String
    ): SignInResponse

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): GetAllStoryResponse

    @GET("stories")
    suspend fun getLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int = 1,
    ): GetAllStoryResponse

    @Multipart
    @POST("stories")
    suspend fun addStoryPhotos(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): AddStoryResponse
}