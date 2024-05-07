package com.example.financieraapp.data.models.credit

import com.example.financieraapp.data.models.client.Client

data class CreditBody(
    val contrato: String,
    val diaPago: String,
    val folio: String,
    val monto: Int,
    val inicial: Int,
    val semanal: Int,
    val cliente: Int,
    val fecha: String,
    val promotora: Int
)

data class ResponseData(
    val message: String,
    val error: Boolean,
    val data: String
)

data class CreditResponse(
    val message : String,
    val error: Boolean,
    val data: List<Credit>
)

data class Credit(
    val id: Int,
    val contrato: String,
    val dia_pago: String,
    val folio: String,
    val monto: Int,
    val pago_inicial: Int,
    val pago_semanal: Int,
    val cliente: Client,
    val fecha: String
)