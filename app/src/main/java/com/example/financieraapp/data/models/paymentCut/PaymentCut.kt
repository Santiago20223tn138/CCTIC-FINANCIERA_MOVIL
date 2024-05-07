package com.example.financieraapp.data.models.paymentCut

data class PaymentCut(
    val message: String,
    val error: Boolean,
    val data: List<PaymentInfo>
)

data class PaymentInfoPdf(
    val message: String,
    val error: Boolean,
    val data: String
)

data class PaymentInfo(
    val contrato_id: Int,
    val telefono_1: String,
    val pago_semanal: Int,
    val contrato: String,
    val folio: String,
    val fecha_maxima: String,
    val monto: Int?,
    val es_fecha_valida: Int,
    val cliente: String
)

data class ResponsePaymentCutData(
    val message: String,
    val error: Boolean,
    val data: List<DataPaymentCut>
)

data class DataPaymentCut(
    val id: Int,
    val idultimo: Int,
    val fecha: String,
    val monto: Int,
    val idcortepago: List<Any>
)

data class ResponseByCorte(
    val message: String,
    val error: Boolean,
    val data: Data
)

data class Data(
    val id: Int,
    val idultimo: Int,
    val fecha: String,
    val monto: Int,
    val idcortepago: List<CortepagoDetail>
)

data class CortepagoDetail(
    val id: Int,
    val id_hisotrico: Historico_pagos?,
    val idcredito: Credito,
    val pagado: Int
)

data class Historico_pagos(
    val id: Int,
    val fecha: String,
    val monto: Int,
    val folio: String,
    val credito: Credito
)

data class Credito(
    val id: Int,
    val contrato: String,
    val dia_pago: String,
    val folio: String,
    val monto: Int,
    val pago_inicial: Int,
    val pago_semanal: Int,
    val semanas: Int,
    val cliente: Cliente,
    val fecha: String
)

data class Cliente(
    val id: Int,
    val nombre: String,
    val numero_cliente: String,
    val paterno: String,
    val materno: String,
    val edad: Int,
    val ingreso_semanal: Int,
    val telefono_1: String,
    val telefono_2: String,
    val correo: String,
    val domicilio: String,
    val domicilio_detalle: String
)