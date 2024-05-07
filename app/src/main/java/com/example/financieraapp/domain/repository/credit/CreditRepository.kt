package com.example.financieraapp.domain.repository.credit

import android.content.Context
import android.preference.PreferenceManager
import com.example.financieraapp.data.models.credit.Credit
import com.example.financieraapp.data.models.credit.CreditBody
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.CreditApiService
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

class CreditRepository(private val context: Context) {

    private val retrofitClient = RetrofitClient()
    private val creditApiService = retrofitClient.getClient(context).create(CreditApiService::class.java)

    suspend fun saveCredit(creditBody: CreditBody) : Boolean{
        return try {
            val response = creditApiService.saveCredit(creditBody)
            response.isSuccessful
        }catch (e:Exception){
            throw Exception("Error al registrar crédito", e)
            false
        }
    }

    suspend fun getCreditsByClient(idClient : Int):List<Credit>{
        try {
            val response = creditApiService.getCreditByClient(idClient)
            if (response.isSuccessful){
                return response.body()?.data ?: emptyList()
            }
        }catch (e:Exception){
            throw Exception("Error al obtener la lista de creditos", e)
        }
        throw Exception("Error al obtener la lista de creditos")
    }

    fun getCurrentDate() :String{
        val format = SimpleDateFormat("yyy-MM-dd")
        val date = Date()
        return format.format(date)
    }

    fun getPromotora() : Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val promotora = preferences.getInt("idPromotora", 0)
        return promotora
    }

}