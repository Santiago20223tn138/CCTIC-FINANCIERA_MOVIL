package com.example.financieraapp.data.models.payment

import com.example.financieraapp.data.models.credit.Credit

data class Payment(
    val contrato_id: Int,
    val pendiente: Int,
    val telefono_1: String,
    val dia_pago: String,
    val pago_semanal: Int,
    val domicilio: String,
    val contrato: String,
    val folio: String,
    val total: Int,
    val pagado: Int,
    val pagos_realizados: Int,
    val fecha_maxima: String,
    val isEs_fecha_valida: Int,
    val cliente: String
)

data class PaymentResponse(
    val message: String,
    val error: Boolean,
    val data: List<Payment>
)

data class PaymentResponseSave(
    val message: String,
    val error: Boolean,
    val data:String
)

data class ReceiptResponse(
    val message: String,
    val error: Boolean,
    val data:String
)

data class PutPayment(
    val id:Int,
    val diaPago:String
)

data class PaymentBody(
    val fecha: String,
    val monto: Int,
    val folio: String,
    val credito: Int
)

data class PaymentResponsePost(
    val message: String,
    val error: Boolean,
    val data: PaymentData
)

data class PaymentData(
    val id: Int,
    val fecha: String,
    val monto: Int,
    val folio: String,
    val credito: Credit
)
