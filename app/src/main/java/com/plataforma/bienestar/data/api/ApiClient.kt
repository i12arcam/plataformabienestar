package com.plataforma.bienestar.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL2 = "http://10.0.2.2:5000"
    // Usar esta para dispositivo físico (reemplaza con tu IP)
    private const val BASE_URL= "https://d48c3c0db7ce.ngrok-free.app"

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