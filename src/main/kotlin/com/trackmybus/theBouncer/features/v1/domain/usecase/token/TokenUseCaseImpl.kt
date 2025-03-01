package com.trackmybus.theBouncer.features.v1.domain.usecase.token

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto
import com.trackmybus.theBouncer.features.v1.domain.repository.jwt.JwtRepository
import io.ktor.util.logging.Logger

class TokenUseCaseImpl(
    private val logger: Logger,
    private val jwtRepository: JwtRepository,
) : TokenUseCase {
    override suspend fun refreshToken(refreshToken: String): Result<TokensDto, RootError> = jwtRepository.refreshAccessToken(refreshToken)

    override suspend fun validateAccessToken(accessToken: String): Result<Boolean, RootError> =
        jwtRepository.validateAccessToken(accessToken)

    override suspend fun validateRefreshToken(refreshToken: String): Result<Boolean, RootError> =
        jwtRepository.validateRefreshToken(refreshToken)

    override suspend fun revokeSession(refreshToken: String): Result<Unit, RootError> = jwtRepository.revokeSession(refreshToken)
}
