package com.example.financieraapp.domain.repository.pagare

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.PagareApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PagareRepository(private val context: Context) {
    private val retrofitClient = RetrofitClient()
    private val pagareApiService = retrofitClient.getClient(context).create(PagareApiService::class.java)

    suspend fun savePagare(id : Int, contract:String) : Boolean{
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val file = File(path, "$contract-pagare.pdf")
        try {
            val response = pagareApiService.getPagare(id)
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
        }catch (e:IOException){
            return false
            Log.i("err", "$e")
        }
    }
}