package com.rocio.organizacionesperanzas

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val teamId: String?,
    val firstName: String,
    val lastName: String,
    val birthDate: String? = null,
    val photoUrl: String? = null,
    var status: PlayerStatus = PlayerStatus.PENDING
) {
    val fullName: String
        get() = "$firstName $lastName"
}
