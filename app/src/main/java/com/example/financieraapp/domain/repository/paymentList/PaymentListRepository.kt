package com.example.financieraapp.domain.repository.paymentList

import android.content.Context
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.PagareApiService
import com.example.financieraapp.data.repository.PaymentListApiService
import retrofit2.create
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PaymentListRepository(private val context: Context) {
    private val retrofitClient = RetrofitClient()
    private val paymentListApi = retrofitClient.getClient(context).create(PaymentListApiService::class.java)

    fun getPromotora() : Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val promotora = preferences.getInt("idPromotora", 0)
        return promotora
    }

    suspend fun savePaymentList() : Boolean{
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val file = File(path, "lista-cobros.pdf")
        try {
            val response = paymentListApi.getPaymentRecord(getPromotora())
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