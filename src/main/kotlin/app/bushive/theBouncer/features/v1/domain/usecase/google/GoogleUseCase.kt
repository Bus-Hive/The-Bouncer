package app.bushive.theBouncer.features.v1.domain.usecase.google

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.domain.dto.TokensDto

interface GoogleUseCase {
    suspend fun login(): Result<String, RootError>

    suspend fun callback(code: String): Result<TokensDto, RootError>

    fun getGoogleLoginUrl(): Result<String, RootError>
}
