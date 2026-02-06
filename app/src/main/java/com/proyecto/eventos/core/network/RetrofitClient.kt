//core/network/RetrofitClient.kt
package com.proyecto.eventos.core.network

import com.proyecto.eventos.BuildConfig
import com.proyecto.eventos.features.eventos.data.remote.EventosApiService
import com.proyecto.eventos.features.login.data.remote.AuthApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val BASE_URL = BuildConfig.BASE_URL

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val eventosApi: EventosApiService by lazy {
        retrofit.create(EventosApiService::class.java)
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}