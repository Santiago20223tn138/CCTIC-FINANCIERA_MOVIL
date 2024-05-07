package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.contract.PDFResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ResponsivaApiService {
    @GET("responsiva/pdf/{id}")
    suspend fun getResponsiva(@Path("id") idContract : Int) : Response<PDFResponse>
}