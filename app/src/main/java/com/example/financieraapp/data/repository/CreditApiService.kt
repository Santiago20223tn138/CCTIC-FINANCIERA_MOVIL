package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.client.ClientBody
import com.example.financieraapp.data.models.client.ClientEntity
import com.example.financieraapp.data.models.client.ClientResponse
import com.example.financieraapp.data.models.credit.CreditBody
import com.example.financieraapp.data.models.credit.CreditResponse
import com.example.financieraapp.data.models.credit.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CreditApiService {
    @POST("client/credito/")
    suspend fun saveCredit(@Body creditBody: CreditBody): Response<ResponseData>

    @GET("client/{id}")
    suspend fun getCreditByClient(@Path("id") idClient:Int) : Response<CreditResponse>
}