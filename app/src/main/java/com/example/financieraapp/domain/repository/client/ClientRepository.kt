package com.example.financieraapp.domain.repository.client

import android.content.Context
import com.example.financieraapp.data.models.client.Client
import com.example.financieraapp.data.models.client.ClientBody
import com.example.financieraapp.data.network.RetrofitClient
import com.example.financieraapp.data.repository.ClientApiService
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale

class ClientRepository(private val context: Context) {

    private val retrofitClient = RetrofitClient()
    private val clientApiService = retrofitClient.getClient(context).create(ClientApiService::class.java)

    suspend fun getClients() : List<Client> {
        try {
            val response = clientApiService.getClients()

            if (response.isSuccessful){
                return response.body()?.data ?: emptyList()
            }
        } catch (e:Exception){
            throw Exception("Error al obtener la lista de clientes", e)
        }
        throw Exception("Error al obtener la lista de clientes: respuesta no exitosa")
    }

    suspend fun saveClient(clientBody: ClientBody) : Boolean{
        return try {
            val response = clientApiService.saveClient(clientBody)
            response.isSuccessful
        }catch (e:Exception){
            throw Exception("Error al registrar cliente", e)
            false
        }

    }

}