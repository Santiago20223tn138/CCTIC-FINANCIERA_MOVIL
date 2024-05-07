package com.example.financieraapp.domain.repository.payment

import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Base64
import com.example.financieraapp.data.models.payment.Payment
import com.example.financieraapp.data.models.payment.PaymentBody
import com.example.financieraapp.data.models.payment.PaymentResponsePost
import com.example.financieraapp.data.models.payment.PutPayment
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.PaymentApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class PaymentRepository(private val context: Context) {

    private val retrofitClient = RetrofitClient()
    private val paymentApiService = retrofitClient.getClient(context).create(PaymentApiService::class.java)

    fun getPromotora() : Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val promotora = preferences.getInt("idPromotora", 0)
        return promotora
    }

    suspend fun getCharges() : List<Payment>{
        try {
            val response = paymentApiService.getCharges(getPromotora())
            if (response.isSuccessful){
                return response.body()?.data ?: emptyList()
            }
        } catch (e: Exception){
            throw Exception("Error al obtener la lista de clientes", e)
        }
        throw Exception("Error al obtener la lista de clientes: respuesta no exitosa")
    }

    suspend fun savePayment(paymentBody: PaymentBody) : Pair<Boolean, PaymentResponsePost?> {
        try {
            val response = paymentApiService.savePayment(paymentBody)
            return Pair(response.isSuccessful, response.body())

        }catch (e:Exception){
            throw Exception("Error al registrar cliente", e)
            return Pair(false, null)
        }
    }

    suspend fun updateDay(putPayment: PutPayment) : Boolean{
        return try {
            val response = paymentApiService.putCredit(putPayment)
            response.isSuccessful
        }catch (e:Exception){
            throw Exception("Error al registrar cliente $e", e)
            false
        }
    }

    fun getCurrentDate() :String{
        val format = SimpleDateFormat("yyy-MM-dd")
        val date = Date()
        return format.format(date)
    }

    suspend fun saveReceiptPdf(id: Int, idCredito : Int, cliente : String, fecha : String) : Boolean{
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val file = File(path, "$idCredito-${cliente.replace(" ", "")}-${fecha.replace("-", "")}.pdf")
        try {
            val response = paymentApiService.getReceipt(id)
            return if (response.isSuccessful){
                val pdfBytes = response.body()?.data ?: throw IOException("Respuesta vacía")
                val decodedPdf = Base64.decode(pdfBytes, Base64.DEFAULT)
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(decodedPdf)
                fileOutputStream.close()
                true
            }else{
                false
            }

        }catch (e: IOException){
            return false
        }

    }
}