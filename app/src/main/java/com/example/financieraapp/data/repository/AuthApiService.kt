package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.auth.AuthEntity
import com.example.financieraapp.data.models.auth.UserInfoEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body authEntity: AuthEntity): Response<UserInfoEntity>
}