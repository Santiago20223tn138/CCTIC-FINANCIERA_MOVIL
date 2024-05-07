package com.example.financieraapp.data.network

import android.content.Context
import com.example.financieraapp.data.core.interceptors.TokenInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    private val URL_BASE:String = "https://www.cctic-apps.com/financiera/";

    private var retrofit: Retrofit? = null;

    fun getClient(context: Context): Retrofit {
        if (retrofit == null){
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(TokenInterceptor(context))

            retrofit = Retrofit.Builder()
                .baseUrl(URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
        }
        return retrofit!!
    }

}