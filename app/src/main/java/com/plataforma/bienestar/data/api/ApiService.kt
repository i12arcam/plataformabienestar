package com.plataforma.bienestar.data.api

import com.plataforma.bienestar.data.api.model.Consejo
import com.plataforma.bienestar.data.api.model.Usuario
import com.plataforma.bienestar.data.api.model.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("api/user")
    suspend fun createUser(@Body user: Usuario): UsuarioResponse

    @GET("api/consejo/select")
    suspend fun getConsejo(@Query("id_usuario") idUsuario: String): Consejo

    // Puedes añadir más endpoints aquí
}