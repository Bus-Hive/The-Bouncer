package com.trackmybus.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenDto(
    val accessToken: String,
)
