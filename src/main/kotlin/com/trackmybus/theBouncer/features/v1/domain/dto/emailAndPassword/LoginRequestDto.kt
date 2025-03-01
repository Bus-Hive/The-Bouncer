package com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String,
)
