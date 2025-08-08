package com.plataforma.bienestar.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL2 = "http://10.0.2.2:5000"
    // Usar esta para dispositivo físico (reemplaza con tu IP)
    private const val BASE_URL = "23.145.163.138:5000"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}