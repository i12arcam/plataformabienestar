package com.plataforma.bienestar.data.api

import com.plataforma.bienestar.data.api.model.Consejo
import com.plataforma.bienestar.data.api.model.Emocion
import com.plataforma.bienestar.data.api.model.Meta
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.data.api.model.Usuario
import com.plataforma.bienestar.data.api.model.UsuarioActividad
import com.plataforma.bienestar.data.api.model.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/user")
    suspend fun createUser(@Body user: Usuario): UsuarioResponse

    @GET("api/consejo/select")
    suspend fun getConsejo(@Query("idUsuario") idUsuario: String): Consejo

    @POST("api/emocion")
    suspend fun createEmocion(@Body emocion: Emocion): Emocion

    @GET("api/emocion/all/{usuarioId}")
    suspend fun getAllEmociones(@Path("usuarioId") usuarioId: String): List<Emocion>

    @GET("api/emocion/recent/{usuarioId}")
    suspend fun getEmocionesRecientes(@Path("usuarioId") usuarioId: String): List<Emocion>

    @GET("api/recurso/select")
    suspend fun getRecursosSeleccionados(@Query("idUsuario") idUsuario: String): List<Recurso>

    @GET("api/recurso/buscar/{parametro}/{filtro}")
    suspend fun getRecursosBusqueda(
        @Path("parametro") parametro: String,
        @Path("filtro") filtro: String
    ): List<Recurso>

    @GET("api/recurso/{idRecurso}")
    suspend fun getRecurso(@Path("idRecurso") idRecurso: String): Recurso

    @GET("api/actividad/estado/{usuarioId}/{recursoId}")
    suspend fun getEstadoActividad(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): String

    @POST("api/actividad/iniciar/{usuarioId}/{recursoId}")
    suspend fun iniciarActividad(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): UsuarioActividad

    @PUT("api/actividad/completar/{usuarioId}/{recursoId}")
    suspend fun completarActividad(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): UsuarioActividad

    @GET("api/actividad/historial/{usuarioId}")
    suspend fun getHistorialActividades(
        @Path("usuarioId") usuarioId: String,
        @Query("estado") estado: String? = null
    ): List<UsuarioActividad>

    @GET("api/meta/activas/{usuarioId}")
    suspend fun getMetasActivas(@Path("usuarioId") usuarioId: String): List<Meta>

    @GET("api/meta/completadas/{usuarioId}")
    suspend fun getMetasCompletadas(@Path("usuarioId") usuarioId: String): List<Meta>

    @GET("api/meta/canceladas/{usuarioId}")
    suspend fun getMetasCanceladas(@Path("usuarioId") usuarioId: String): List<Meta>

    @POST("api/meta")
    suspend fun createMeta(@Body meta: Meta): UsuarioResponse

    @PUT("api/meta/progreso/{metaId}")
    suspend fun incrementarDiasMeta(
        @Path("metaId") recursoId: String
    ): UsuarioActividad

    @PUT("api/meta/cancelar/{metaId}")
    suspend fun cancelarMeta(
        @Path("metaId") recursoId: String
    ): UsuarioActividad

    @PUT("api/meta/reanudar/{metaId}")
    suspend fun reanudarMeta(
        @Path("metaId") recursoId: String
    ): UsuarioActividad

    @DELETE("api/meta/{metaId}")
    suspend fun eliminarMeta(
        @Path("metaId") recursoId: String
    ): UsuarioActividad


}