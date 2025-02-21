package com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError

interface PasswordHashRepository {
    fun hashPassword(password: String): Result<String, RootError>

    fun verifyPassword(
        password: String,
        hashedPassword: String,
    ): Result<Boolean, RootError>
}
