package app.bushive.theBouncer.features.v1.domain.usecase.emailPassword

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.domain.dto.TokensDto

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
