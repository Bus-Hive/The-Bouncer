package app.bushive.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class TokensDto(
    val accessToken: String,
    val refreshToken: String,
)
