package com.example.financieraapp.domain.repository.auth

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import com.example.financieraapp.data.repository.AuthApiService
import com.example.financieraapp.data.models.auth.AuthEntity
import com.example.financieraapp.data.models.auth.UserInfoDetailsEntity
import com.example.financieraapp.data.models.auth.UserInfoEntity
import com.example.financieraapp.data.network.RetrofitClient

class AuthRepository(private val context: Context){

    private val retrofitClient = RetrofitClient()
    private val authApiService = retrofitClient.getClient(context).create(AuthApiService::class.java)


    private fun saveToken(userInfo: UserInfoEntity){
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("authToken", userInfo.data.token)
        editor.putInt("idPromotora", userInfo.data.userInfo.id)
        editor.putString("nombreUsuario", "${userInfo.data.userInfo.person.name} ${userInfo.data.userInfo.person.lastName}")
        editor.apply()
    }

    private fun saveCredentials(authEntity: AuthEntity){
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString("username", authEntity.username)
        editor.putString("password", authEntity.password)
        editor.apply()
    }

    suspend fun login(authEntity: AuthEntity) :Pair<Boolean, Int?> {
        return try {
            val response = authApiService.login(authEntity)
            if (response.isSuccessful){
                if (response.body()?.data?.userInfo?.status == 1){
                    response?.body()?.let { saveToken(it) }
                    saveCredentials(authEntity)
                }
            }
            return Pair(response.isSuccessful, response.body()?.data?.userInfo?.status)
        }catch (e:Exception){
            throw Exception("Error al iniciar sesión", e)
            return Pair(false, null)
        }
    }

}
