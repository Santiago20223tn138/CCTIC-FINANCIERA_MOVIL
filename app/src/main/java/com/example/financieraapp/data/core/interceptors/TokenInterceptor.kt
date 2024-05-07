package com.example.financieraapp.data.core.interceptors

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest: Request = chain.request()

        val authToken = getAuthTokenFromPreferences(context)

        if (authToken!=null && authToken.isNotEmpty()){
            val newRequest: Request = currentRequest.newBuilder()
                .header("Authorization", "Bearer $authToken")
                .build()

            return chain.proceed(newRequest)
        }

        return chain.proceed(currentRequest)
    }

    private fun getAuthTokenFromPreferences(context: Context) :String?{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return  preferences.getString("authToken", null)
    }
}