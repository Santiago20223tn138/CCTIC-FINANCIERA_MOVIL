package com.example.financieraapp.data.repository

import com.example.financieraapp.data.models.payment.PaymentBody
import com.example.financieraapp.data.models.payment.PaymentResponse
import com.example.financieraapp.data.models.payment.PaymentResponsePost
import com.example.financieraapp.data.models.payment.PaymentResponseSave
import com.example.financieraapp.data.models.payment.PutPayment
import com.example.financieraapp.data.models.payment.ReceiptResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApiService {

    @GET("credito/consultaListaPorCobrar/{id}")
    suspend fun getCharges(@Path("id") idPromotora:Int) : Response<PaymentResponse>

    @POST("credito/")
    suspend fun savePayment(@Body paymentBody: PaymentBody):Response<PaymentResponsePost>

    @PUT("client/credito/")
    suspend fun putCredit(@Body putPayment: PutPayment):Response<PaymentResponseSave>

    @GET("recibo-pago/pdf/{id}")
    suspend fun getReceipt(@Path("id") idPayment:Int):Response<ReceiptResponse>
}