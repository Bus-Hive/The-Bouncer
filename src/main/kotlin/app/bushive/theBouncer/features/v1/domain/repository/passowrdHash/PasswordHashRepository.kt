package app.bushive.theBouncer.features.v1.domain.repository.passowrdHash

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError

interface PasswordHashRepository {
    fun hashPassword(password: String): Result<String, RootError>

    fun verifyPassword(
        password: String,
        hashedPassword: String,
    ): Result<Boolean, RootError>
}
