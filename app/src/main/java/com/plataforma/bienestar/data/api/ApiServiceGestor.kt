package com.plataforma.bienestar.data.api

import com.plataforma.bienestar.data.api.model.Consejo
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.ProgramaNuevo
import com.plataforma.bienestar.data.api.model.Recurso
import retrofit2.http.*

interface ApiServiceGestor {
    // Consejos
    @GET("api/consejo/all")
    suspend fun getConsejos(@Query("idUsuario") idUsuario: String): List<Consejo>

    @POST("api/consejo")
    suspend fun createConsejo(
        @Query("idUsuario") idUsuario: String,
        @Body consejo: Consejo
    ): Consejo

    @PUT("api/consejo/{id}")
    suspend fun updateConsejo(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String,
        @Body consejo: Consejo
    ): Consejo

    @DELETE("api/consejo/{id}")
    suspend fun deleteConsejo(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String
    )

    // Recursos
    @GET("api/recurso/all")
    suspend fun getRecursos(@Query("idUsuario") idUsuario: String): List<Recurso>

    @POST("api/recurso")
    suspend fun createRecurso(
        @Query("idUsuario") idUsuario: String,
        @Body recurso: Recurso
    ): Recurso

    @PUT("api/recurso/{id}")
    suspend fun updateRecurso(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String,
        @Body recurso: Recurso
    ): Recurso

    @DELETE("api/recurso/{id}")
    suspend fun deleteRecurso(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String
    )

    // Programas
    @GET("api/programa/all")
    suspend fun getProgramas(@Query("idUsuario") idUsuario: String): List<Programa>

    @POST("api/programa")
    suspend fun createPrograma(
        @Query("idUsuario") idUsuario: String,
        @Body programa: ProgramaNuevo
    ): Programa

    @PUT("api/programa/{id}")
    suspend fun updatePrograma(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String,
        @Body programa: ProgramaNuevo
    ): Programa

    @DELETE("api/programa/{id}")
    suspend fun deletePrograma(
        @Path("id") id: String,
        @Query("idUsuario") idUsuario: String
    )
}