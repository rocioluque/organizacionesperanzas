package com.rocio.organizacionesperanzas

/**
 * This file contains all the data transfer objects (DTOs) used for API communication.
 */

// Models for the data structures themselves

data class Category(val id: String, val name: String)

data class Team(val id: String, val name: String, val categories: List<Category>)

data class User(
    val id: String,
    val username: String,
    val role: UserRole,
    val assignedTeams: List<String>? // List of team IDs, nullable
)

data class AssignedCategory(val teamName: String, val category: Category)

// Request/Response bodies

data class LoginRequest(val username: String, val password: String)

data class LoginResponse(val userId: String, val token: String, val role: UserRole)

data class PlayerUpdateRequest(val player: Player)

data class StatusUpdateRequest(val status: PlayerStatus)

// Models for creating/updating data

data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: UserRole,
    val assignedTeams: List<String>?
)
