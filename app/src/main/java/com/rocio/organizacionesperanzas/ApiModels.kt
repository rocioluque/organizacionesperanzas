package com.rocio.organizacionesperanzas

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// --- AUTHENTICATION ---
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val id: String, val role: UserRole)

// --- USER ---
data class User(val id: String, val username: String, val role: UserRole, val assignedTeams: List<String> = emptyList())
data class CreateUserRequest(val username: String, val password: String, val role: UserRole, val assignedTeams: List<String> = emptyList())
enum class UserRole : Serializable { ADMIN, ORGANIZER, DELEGATE }

// --- CATEGORY ---
data class Category(val id: String, val name: String)

// --- TEAM ---
data class Team(val id: String, val name: String, val playerCount: Int, val categories: List<Category>)

data class TeamRequest(val name: String, @SerializedName("categoryIds") val categoryIds: List<String>)

// --- PLAYER ---
enum class PlayerStatus : Serializable { APPROVED, PENDING, REJECTED }
data class StatusUpdateRequest(val status: PlayerStatus)
