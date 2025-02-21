package com.trackmybus.theBouncer.features.v1.domain.repository.jwt

import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface JwtRepository {
    suspend fun generateAccessToken(user: User): Result<String>

    suspend fun generateRefreshToken(user: User): Result<String>

    suspend fun validateAccessToken(accessToken: String): Result<Boolean>

    suspend fun validateRefreshToken(refreshToken: String): Result<Boolean>

    suspend fun refreshAccessToken(refreshToken: String): Result<TokensDto>

    suspend fun revokeSession(refreshToken: String): Result<Unit>
}
