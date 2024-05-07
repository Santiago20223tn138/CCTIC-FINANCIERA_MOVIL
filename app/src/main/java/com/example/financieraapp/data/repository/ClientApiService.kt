package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.auth.AuthEntity
import com.example.financieraapp.data.models.auth.UserInfoEntity
import com.example.financieraapp.data.models.client.ClientBody
import com.example.financieraapp.data.models.client.ClientEntity
import com.example.financieraapp.data.models.client.ClientResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ClientApiService {
    @GET("client/")
    suspend fun getClients() : Response<ClientEntity>

    @POST("client/cliente/")
    suspend fun saveClient(@Body clientBody: ClientBody): Response<ClientResponse>
}