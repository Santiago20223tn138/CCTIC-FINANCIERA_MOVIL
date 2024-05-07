package com.example.financieraapp.domain.repository.contract

import android.content.Context
import android.os.Environment
import android.util.Base64
import android.util.Log
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.ContractApiService
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ContractRepository(private val context: Context) {
    private val retrofitClient = RetrofitClient()
    private val contractApiService = retrofitClient.getClient(context).create(ContractApiService::class.java)



    suspend fun saveContractPdf(id: Int, contract : String) : Boolean{
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val file = File(path, "$contract-contrato.pdf")
        try {
            val response = contractApiService.getContract(id)
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

        }catch (e:IOException){
            return false
            Log.i("hola", "$e")
        }

    }
}