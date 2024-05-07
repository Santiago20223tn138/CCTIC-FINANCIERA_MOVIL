package com.example.financieraapp.data.models.contract

data class Contract(
    val date: String,
    val clientName: String,
    val clientPhone: String,
    val clientDomicile:String,
    val contractNumber: String,
    val folio: String,
    val clientNumber: String,
    val sucursal : Int,
    val amountGiven: Double,
    val weeklyTerm: Int,
    val minInitialPayment : Double,
    val weeklyPayment: Double,
    val amountToPay: Double
)

data class PDFResponse(
    val message: String,
    val error: Boolean,
    val data: String
)
