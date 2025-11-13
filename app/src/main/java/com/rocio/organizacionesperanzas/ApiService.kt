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

data class UploadResponse(val url: String)

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @Multipart
    @POST("upload")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): UploadResponse

    @GET("users")
    suspend fun getUsers(): List<User>

    @POST("users")
    suspend fun addUser(@Body user: CreateUserRequest): User

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: CreateUserRequest): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: String)

    @GET("categories")
    suspend fun getAllCategories(): List<Category>

    @POST("categories")
    suspend fun addCategory(@Body category: Category): Category

    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body category: Category): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    @GET("categories/{userId}")
    suspend fun getAssignedCategories(@Path("userId") userId: String): List<AssignedCategory>

    @GET("teams")
    suspend fun getAllTeams(): List<Team>

    @POST("teams")
    suspend fun addTeam(@Body team: Team): Team

    @PUT("teams/{id}")
    suspend fun updateTeam(@Path("id") id: String, @Body team: Team): Team

    @DELETE("teams/{id}")
    suspend fun deleteTeam(@Path("id") id: String)

    @GET("teams/by-category/{categoryId}")
    suspend fun getTeamsByCategory(@Path("categoryId") categoryId: String): List<Team>

    @GET("players/by-category/{categoryId}")
    suspend fun getPlayers(@Path("categoryId") categoryId: String): List<Player>

    @GET("players/{playerId}")
    suspend fun getPlayerById(@Path("playerId") playerId: String): Player

    @POST("players")
    suspend fun addPlayer(@Body player: Player): Player

    @DELETE("players/{id}")
    suspend fun deletePlayer(@Path("id") id: String)

    @PUT("players/{playerId}")
    suspend fun updatePlayerDetails(@Path("playerId") playerId: String, @Body player: Player)

    @PUT("players/{playerId}/status")
    suspend fun updatePlayerStatus(@Path("playerId") playerId: String, @Body request: StatusUpdateRequest)
}