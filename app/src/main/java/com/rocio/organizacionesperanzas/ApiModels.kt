package com.rocio.organizacionesperanzas

// --- AUTHENTICATION ---
data class LoginRequest(val email: String, val pass: String)
data class LoginResponse(val token: String, val user: User)

// --- USER ---
data class User(val id: String, val email: String, val role: UserRole)
data class CreateUserRequest(val email: String, val pass: String, val role: UserRole)
enum class UserRole { ADMIN, ORGANIZER, DELEGATE }

// --- CATEGORY ---
data class Category(val id: String, val name: String)
data class AssignedCategory(val id: String, val name: String, val category: Category)

// --- TEAM ---
data class Team(val id: String, val name: String)

// --- PLAYER ---
enum class PlayerStatus { APPROVED, PENDING, REJECTED }
data class StatusUpdateRequest(val status: PlayerStatus)
