package com.trackmybus.theBouncer.features.v1.domain.repository.jwt

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface JwtRepository {
    suspend fun generateAccessToken(user: User): Result<String, RootError>

    suspend fun generateRefreshToken(user: User): Result<String, RootError>

    suspend fun validateAccessToken(accessToken: String): Result<Boolean, RootError>

    suspend fun validateRefreshToken(refreshToken: String): Result<Boolean, RootError>

    suspend fun refreshAccessToken(refreshToken: String): Result<TokensDto, RootError>

    suspend fun revokeSession(refreshToken: String): Result<Unit, RootError>
}
