package com.plataforma.bienestar.data.api

import com.plataforma.bienestar.data.api.model.Consejo
import com.plataforma.bienestar.data.api.model.Emocion
import com.plataforma.bienestar.data.api.model.EmocionesHistorialResponse
import com.plataforma.bienestar.data.api.model.Meta
import com.plataforma.bienestar.data.api.model.Programa
import com.plataforma.bienestar.data.api.model.Recurso
import com.plataforma.bienestar.data.api.model.Usuario
import com.plataforma.bienestar.data.api.model.UsuarioPrograma
import com.plataforma.bienestar.data.api.model.UsuarioRecurso
import com.plataforma.bienestar.data.api.model.UsuarioResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Usuarios
    @POST("api/user")
    suspend fun createUser(@Body user: Usuario): UsuarioResponse

    // Consejos
    @GET("api/consejo/select")
    suspend fun getConsejo(@Query("idUsuario") idUsuario: String): Consejo

    // Emociones
    @POST("api/emocion")
    suspend fun createEmocion(@Body emocion: Emocion): Emocion

    @GET("api/emocion/all/{usuarioId}")
    suspend fun getAllEmociones(@Path("usuarioId") usuarioId: String): List<Emocion>

    @GET("api/emocion/historial/{usuarioId}")
    suspend fun getEmocionesHistorial(@Path("usuarioId") usuarioId: String): EmocionesHistorialResponse

    // Recursos
    @GET("api/recurso/select")
    suspend fun getRecursosSeleccionados(@Query("idUsuario") idUsuario: String): List<Recurso>

    @GET("api/recurso/buscar/{parametro}/{filtro}")
    suspend fun getRecursosBusqueda(
        @Path("parametro") parametro: String,
        @Path("filtro") filtro: String
    ): List<Recurso>

    @GET("api/recurso/{idRecurso}")
    suspend fun getRecurso(@Path("idRecurso") idRecurso: String): Recurso

    // Usuarios Recursos
    @GET("api/usuarioRecurso/estado/{usuarioId}/{recursoId}")
    suspend fun getEstadoRecurso(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): String

    @PUT("api/usuarioRecurso/iniciar/{usuarioId}/{recursoId}")
    suspend fun iniciarActividad(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): UsuarioRecurso

    @PUT("api/usuarioRecurso/completar/{usuarioId}/{recursoId}")
    suspend fun completarActividad(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): UsuarioRecurso

    @POST("api/usuarioRecurso/setVisto/{usuarioId}/{recursoId}")
    suspend fun setRecursoVisto(
        @Path("usuarioId") usuarioId: String,
        @Path("recursoId") recursoId: String
    ): UsuarioRecurso

    @GET("api/usuarioRecurso/historial/{usuarioId}")
    suspend fun getHistorialRecursos(
        @Path("usuarioId") usuarioId: String,
        @Query("estado") estado: String? = null
    ): List<UsuarioRecurso>

    // Programas
    @GET("api/programa/select")
    suspend fun getProgramasSeleccionados(@Query("idUsuario") idUsuario: String): List<Programa>

    @GET("api/programa/{idPrograma}")
    suspend fun getPrograma(@Path("idPrograma") idPrograma: String): Programa

    @GET("api/programa/buscar/{parametro}/{filtro}")
    suspend fun getProgramasBusqueda(
        @Path("parametro") parametro: String,
        @Path("filtro") filtro: String
    ): List<Programa>

    // Usuarios Programas

    @POST("api/usuarioPrograma/iniciar/{usuarioId}/{programaId}/{totalRecursos}")
    suspend fun iniciarPrograma(
        @Path("usuarioId") usuarioId: String,
        @Path("programaId") programaId: String,
        @Path("totalRecursos") totalRecursos: Number,
    ): UsuarioPrograma

    @GET("api/usuarioPrograma/{usuarioId}/{programaId}")
    suspend fun obtenerUsuarioPrograma(
        @Path("usuarioId") usuarioId: String,
        @Path("programaId") programaId: String
    ): UsuarioPrograma

    @PUT("api/usuarioPrograma/{usuarioId}/{programaId}/{posicion}/{estado}")
    suspend fun actualizarEstadoRecursoPrograma(
        @Path("usuarioId") usuarioId: String,
        @Path("programaId") programaId: String,
        @Path("posicion") posicion: Number,
        @Path("estado") estado: String
    ): UsuarioPrograma

    @GET("api/usuarioPrograma/historial/{usuarioId}")
    suspend fun getHistorialProgramas(
        @Path("usuarioId") usuarioId: String,
        @Query("estado") estado: String? = null
    ): List<UsuarioPrograma>

    // Metas
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
    ): Meta

    @PUT("api/meta/cancelar/{metaId}")
    suspend fun cancelarMeta(
        @Path("metaId") recursoId: String
    ): Meta

    @PUT("api/meta/reanudar/{metaId}")
    suspend fun reanudarMeta(
        @Path("metaId") recursoId: String
    ): Meta

    @DELETE("api/meta/{metaId}")
    suspend fun eliminarMeta(
        @Path("metaId") recursoId: String
    ): Meta


}