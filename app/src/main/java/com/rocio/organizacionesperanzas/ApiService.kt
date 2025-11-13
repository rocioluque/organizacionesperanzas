package com.rocio.organizacionesperanzas

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

// Respuesta para la subida de fotos - contiene la URL donde se almacenó la imagen
data class UploadResponse(val url: String)

// Interfaz que define todas las operaciones de la API con el servidor
interface ApiService {

    // Inicia sesión en el sistema con usuario y contraseña
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Sube una imagen al servidor usando formato multipart (para archivos)
    @Multipart
    @POST("upload")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): UploadResponse

    // Obtiene la lista de todos los usuarios del sistema
    @GET("users")
    suspend fun getUsers(): List<User>

    // Crea un nuevo usuario en el sistema
    @POST("users")
    suspend fun addUser(@Body user: CreateUserRequest): User

    // Actualiza los datos de un usuario existente
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: CreateUserRequest): User

    // Elimina un usuario del sistema
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String)

    // Obtiene todas las categorías disponibles
    @GET("categories")
    suspend fun getAllCategories(): List<Category>

    // Crea una nueva categoría
    @POST("categories")
    suspend fun addCategory(@Body category: Category): Category

    // Actualiza una categoría existente
    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body category: Category): Category

    // Elimina una categoría
    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    // Obtiene todos los equipos del sistema
    @GET("teams")
    suspend fun getAllTeams(): List<Team>

    // CORREGIDO: Ahora usa TeamRequest para enviar los datos
    @POST("teams")
    suspend fun addTeam(@Body team: TeamRequest): Team

    // CORREGIDO: Ahora usa TeamRequest para enviar los datos
    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: String, @Body team: TeamRequest): Team

    // Elimina un equipo
    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: String)

    // Obtiene los equipos filtrados por una categoría específica (vista de organizador)
    @GET("teams/by-category/{categoryId}")
    suspend fun getTeamsByCategory(@Path("categoryId") categoryId: String): List<Team>

    // Obtiene los jugadores de una categoría específica
    @GET("players/by-category/{categoryId}")
    suspend fun getPlayers(@Path("categoryId") categoryId: String): List<Player>

    // Obtiene los datos de un jugador específico por su ID
    @GET("players/{playerId}")
    suspend fun getPlayerById(@Path("playerId") playerId: String): Player

    // Crea un nuevo jugador
    @POST("players")
    suspend fun addPlayer(@Body player: Player): Player

    // Elimina un jugador
    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id") id: String)

    // Actualiza todos los datos de un jugador (información completa)
    @PUT("players/{playerId}")
    suspend fun updatePlayerDetails(@Path("playerId") playerId: String, @Body player: Player)

    // Actualiza solo el estado de un jugador (actualización parcial)
    @PUT("players/{playerId}/status")
    suspend fun updatePlayerStatus(@Path("playerId") playerId: String, @Body request: StatusUpdateRequest)
}