package com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)
