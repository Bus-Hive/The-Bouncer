package com.trackmybus.theBouncer.features.v1.domain.usecase.token

import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface TokenUseCase {
    suspend fun refreshToken(refreshToken: String): Result<TokensDto>

    suspend fun validateAccessToken(accessToken: String): Result<Boolean>

    suspend fun validateRefreshToken(refreshToken: String): Result<Boolean>

    suspend fun revokeSession(refreshToken: String): Result<Unit>
}
