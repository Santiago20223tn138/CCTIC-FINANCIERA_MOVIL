package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.contract.PDFResponse
import com.example.financieraapp.data.models.paymentCut.PaymentCut
import com.example.financieraapp.data.models.paymentCut.PaymentInfoPdf
import com.example.financieraapp.data.models.paymentCut.ResponseByCorte
import com.example.financieraapp.data.models.paymentCut.ResponsePaymentCutData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentCutApiService {
    @POST("credito/CorteDePago/")
    suspend fun doPaymentCut() : Response<PaymentCut>

    @GET("Corte/promotora/{id}")
    suspend fun getPaymentsCut(@Path("id") idCorte:Int) : Response<ResponsePaymentCutData>

    @GET("Corte/ByIdCortePago/{id}")
    suspend fun getDetailPaymentCut(@Path("id") idCorte:Int) : Response<ResponseByCorte>

    @GET("cortePago-info/pdf/{id}")
    suspend fun getPdf(@Path("id") idCorte:Int) : Response<PaymentInfoPdf>

}