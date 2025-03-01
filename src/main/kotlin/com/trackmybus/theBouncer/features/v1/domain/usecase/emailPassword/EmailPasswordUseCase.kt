package com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface EmailPasswordUseCase {
    suspend fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
    ): Result<Unit, RootError>

    suspend fun loginUser(
        email: String,
        password: String,
    ): Result<TokensDto, RootError>
}
