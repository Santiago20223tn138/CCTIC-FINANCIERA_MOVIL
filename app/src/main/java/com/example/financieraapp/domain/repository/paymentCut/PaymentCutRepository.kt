package com.example.financieraapp.domain.repository.paymentCut

import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import com.example.financieraapp.data.models.paymentCut.CortepagoDetail
import com.example.financieraapp.data.models.paymentCut.DataPaymentCut
import com.example.financieraapp.data.models.paymentCut.PaymentCut
import com.example.financieraapp.data.models.paymentCut.PaymentInfo
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.PaymentCutApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class PaymentCutRepository(private val context: Context) {
    private val retrofitClient = RetrofitClient()
    private val paymentCutApiService = retrofitClient.getClient(context).create(PaymentCutApiService::class.java)
    private lateinit var paid : List<CortepagoDetail>
    private lateinit var notPaid : List<CortepagoDetail>
    private var monto : Int = 0
    private var date : String = ""


    suspend fun getPaymentsCut(): List<DataPaymentCut> {
        try {
            val response = paymentCutApiService.getPaymentsCut(getPromotora())
            if (response.isSuccessful) {
                val data = response.body()?.data ?: emptyList()
                return data
            }
        } catch (e: Exception) {
            throw Exception("Error al obtener los cortes", e)
        }
        throw Exception("Error al obtener los cortes")
    }

    fun getPromotora() : Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val promotora = preferences.getInt("idPromotora", 0)
        return promotora
    }

    suspend fun getByPaymentCut(idCorte : Int) {
        try {
            val response = paymentCutApiService.getDetailPaymentCut(idCorte);
            if (response.isSuccessful){
                val data = response.body()?.data
                val detail = data?.idcortepago ?: emptyList()
                val (paidClients, notPaidClients) = detail.partition { it.pagado == 1 }
                this.paid = paidClients
                this.notPaid = notPaidClients
                if (data != null) {
                    this.monto = data.monto
                    this.date = getCurrentDate(data.fecha)
                }
            }
        } catch (e: Exception){
            throw Exception("Error al hacer el corte de pago", e)
        }
    }

    fun getPaid(): List<CortepagoDetail> {
        return paid
    }

    fun getNotPaid(): List<CortepagoDetail> {
        return notPaid
    }

    fun getMonto(): Int{
        return monto
    }

    fun getDate(): String{
        return date
    }

    fun getCurrentDate(date : String) :String{
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val dateF = inputFormat.parse(date)
        return outputFormat.format(dateF)
    }

    suspend fun saveCorteDetalles(idCorte: Int) : Boolean{
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val file = File(path, "$idCorte-detalleCorte.pdf")
        try {
            val response = paymentCutApiService.getPdf(idCorte)
            if (response.isSuccessful){
                val pdfBytes = response.body()?.data ?: throw IOException("Respuesta vacía")
                val decodedPdf = Base64.decode(pdfBytes, Base64.DEFAULT)
                val fileOutputStream = FileOutputStream(file)
                fileOutputStream.write(decodedPdf)
                fileOutputStream.close()
                return true
            }else{
                return false
            }
        }catch (e: IOException){
            return false
            Log.i("err", "$e")
        }
    }

}