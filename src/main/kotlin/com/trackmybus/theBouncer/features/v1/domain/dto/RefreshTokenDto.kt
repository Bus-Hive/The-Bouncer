package com.trackmybus.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenDto(
    val refreshToken: String,
)
