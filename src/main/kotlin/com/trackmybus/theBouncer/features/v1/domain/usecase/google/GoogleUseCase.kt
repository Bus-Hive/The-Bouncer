package com.trackmybus.theBouncer.features.v1.domain.usecase.google

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto

interface GoogleUseCase {
    suspend fun login(): Result<String, RootError>

    suspend fun callback(code: String): Result<TokensDto, RootError>

    fun getGoogleLoginUrl(): Result<String, RootError>
}
