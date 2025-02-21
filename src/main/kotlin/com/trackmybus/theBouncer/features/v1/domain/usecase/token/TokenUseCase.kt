package com.trackmybus.theBouncer.features.v1.domain.usecase.token

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface TokenUseCase {
    suspend fun refreshToken(refreshToken: String): Result<TokensDto, RootError>

    suspend fun validateAccessToken(accessToken: String): Result<Boolean, RootError>

    suspend fun validateRefreshToken(refreshToken: String): Result<Boolean, RootError>

    suspend fun revokeSession(refreshToken: String): Result<Unit, RootError>
}
