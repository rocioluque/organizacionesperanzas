package com.rocio.organizacionesperanzas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// --- AUTHENTICATION ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val id: String, val role: UserRole)

// --- USER ---
data class User(val id: String, val email: String, val role: UserRole)
data class CreateUserRequest(val email: String, val password: String, val role: UserRole)
enum class UserRole : Serializable { ADMIN, ORGANIZER, DELEGATE }

// --- CATEGORY ---
data class Category(val id: String, val name: String)

// --- TEAM ---
// Modelo para RECIBIR equipos de la API (con objetos Category completos)
data class Team(val id: String, val name: String, val playerCount: Int, val categories: List<Category>)

// Modelo para ENVIAR equipos a la API (solo con los IDs de categoría)
data class TeamRequest(val name: String, @SerializedName("categoryIds") val categoryIds: List<String>)


// --- PLAYER ---
enum class PlayerStatus : Serializable { APPROVED, PENDING, REJECTED }
data class StatusUpdateRequest(val status: PlayerStatus)
