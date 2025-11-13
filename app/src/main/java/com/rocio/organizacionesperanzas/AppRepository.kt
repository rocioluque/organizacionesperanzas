package com.rocio.organizacionesperanzas

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import retrofit2.HttpException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object AppRepository {

    private val apiService: ApiService = RetrofitClient.instance
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    // --- AUTH ---
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            Result.Success(apiService.login(request))
        } catch (e: HttpException) {
            println("API HTTP Error on Login: Code = ${e.code()}, Body = ${e.response()?.errorBody()?.string()}")
            Result.Error("Usuario o contraseña incorrectos")
        } catch (e: Exception) {
            println("API Network Error on Login: ${e.message}")
            Result.Error("Error de red. Por favor, intente más tarde.")
        }
    }

    // --- PHOTO UPLOAD (Cloudinary) ---
    suspend fun uploadPhotoToCloudinary(photoUri: Uri): String? = suspendCoroutine { continuation ->
        MediaManager.get().upload(photoUri)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    println("Cloudinary upload started...")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as? String
                    println("Cloudinary upload success: $secureUrl")
                    continuation.resume(secureUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    println("Cloudinary upload error: ${error.description}")
                    continuation.resume(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    println("Cloudinary upload rescheduled: ${error.description}")
                }
            }).dispatch()
    }


    // --- USERS (Management) ---
    suspend fun getUsers(): List<User> {
        return try {
            apiService.getUsers()
        } catch (e: Exception) {
            println("API ERROR: Get users failed - ${e.message}")
            emptyList()
        }
    }

    suspend fun addUser(user: CreateUserRequest): User? {
        return try {
            apiService.addUser(user)
        } catch (e: Exception) {
            println("API ERROR: Add user failed - ${e.message}")
            null
        }
    }

    suspend fun updateUser(id: String, user: CreateUserRequest): User? {
        return try {
            apiService.updateUser(id, user)
        } catch (e: Exception) {
            println("API ERROR: Update user failed - ${e.message}")
            null
        }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            apiService.deleteUser(id)
            true
        } catch (e: Exception) {
            println("API ERROR: Delete user failed - ${e.message}")
            false
        }
    }

    // --- CATEGORIES (Management) ---
    suspend fun getAllCategories(): List<Category> {
        return try {
            apiService.getAllCategories()
        } catch (e: Exception) {
            println("API ERROR: Get all categories failed - ${e.message}")
            emptyList()
        }
    }

    suspend fun addCategory(category: Category): Category? {
        return try {
            apiService.addCategory(category)
        } catch (e: Exception) {
            println("API ERROR: Add category failed - ${e.message}")
            null
        }
    }

    suspend fun updateCategory(id: String, category: Category): Category? {
        return try {
            apiService.updateCategory(id, category)
        } catch (e: Exception) {
            println("API ERROR: Update category failed - ${e.message}")
            null
        }
    }

    suspend fun deleteCategory(id: String): Boolean {
        return try {
            apiService.deleteCategory(id)
            true
        } catch (e: Exception) {
            println("API ERROR: Delete category failed - ${e.message}")
            false
        }
    }

    // --- TEAMS (Management) ---
    suspend fun getAllTeams(): List<Team> {
        return try {
            apiService.getAllTeams()
        } catch (e: Exception) {
            println("API ERROR: Get all teams failed - ${e.message}")
            emptyList()
        }
    }

    suspend fun addTeam(team: Team): Team? {
        return try {
            apiService.addTeam(team)
        } catch (e: Exception) {
            println("API ERROR: Add team failed - ${e.message}")
            null
        }
    }

    suspend fun updateTeam(id: String, team: Team): Team? {
        return try {
            apiService.updateTeam(id, team)
        } catch (e: Exception) {
            println("API ERROR: Update team failed - ${e.message}")
            null
        }
    }

    suspend fun deleteTeam(id: String): Boolean {
        return try {
            apiService.deleteTeam(id)
            true
        } catch (e: Exception) {
            println("API ERROR: Delete team failed - ${e.message}")
            false
        }
    }

    // --- TEAMS (Organizer View) ---
    suspend fun getTeamsByCategory(categoryId: String): List<Team> {
        return try {
            println("API CALL: Getting teams for categoryId: $categoryId")
            val teams = apiService.getTeamsByCategory(categoryId)
            println("API SUCCESS: Found ${teams.size} teams for categoryId: $categoryId")
            teams
        } catch (e: HttpException) {
            println("API HTTP Error on getTeamsByCategory: Code = ${e.code()}, Body = ${e.response()?.errorBody()?.string()}")
            emptyList()
        } catch (e: Exception) {
            println("API Network Error on getTeamsByCategory: ${e.message}")
            emptyList()
        }
    }

    // --- PLAYERS ---
    suspend fun getPlayers(categoryId: String): List<Player> {
        return try {
            apiService.getPlayers(categoryId)
        } catch (e: Exception) {
            println("API ERROR: Get players for category $categoryId failed - ${e.message}")
            emptyList()
        }
    }

    suspend fun getPlayerById(playerId: String): Player? {
        return try {
            println("API CALL: Getting player by ID: $playerId")
            apiService.getPlayerById(playerId)
        } catch (e: HttpException) {
            println("API HTTP Error on getPlayerById: Code = ${e.code()}, Body = ${e.response()?.errorBody()?.string()}, ID: $playerId")
            null
        } catch (e: Exception) {
            println("API Network Error on getPlayerById for ID $playerId: ${e.message}")
            null
        }
    }

    suspend fun addPlayer(player: Player): Player? {
        return try {
            apiService.addPlayer(player)
        } catch (e: Exception) {
            println("API ERROR: Add player failed - ${e.message}")
            null
        }
    }
    
    suspend fun deletePlayer(id: String): Boolean {
        return try {
            apiService.deletePlayer(id)
            true
        } catch (e: Exception) {
            println("API ERROR: Delete player failed - ${e.message}")
            false
        }
    }

    suspend fun updatePlayerDetails(playerId: String, updatedPlayer: Player) {
        try {
            apiService.updatePlayerDetails(playerId, updatedPlayer)
        } catch (e: HttpException) {
            println("API HTTP Error on updatePlayerDetails: Code = ${e.code()}, Body = ${e.response()?.errorBody()?.string()}, ID: $playerId")
        } catch (e: Exception) {
            println("API Network Error on updatePlayerDetails for ID $playerId: ${e.message}")
        }
    }

    suspend fun updatePlayerStatus(playerId: String, newStatus: PlayerStatus) {
        try {
            apiService.updatePlayerStatus(playerId, StatusUpdateRequest(newStatus))
        } catch (e: Exception) {
            println("API ERROR: Update status failed - ${e.message}.")
        }
    }
}