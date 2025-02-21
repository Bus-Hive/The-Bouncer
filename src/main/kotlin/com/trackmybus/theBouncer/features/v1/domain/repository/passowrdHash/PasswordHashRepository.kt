package com.trackmybus.theBouncer.features.v1.domain.repository.passowrdHash

interface PasswordHashRepository {
    fun hashPassword(password: String): String

    fun verifyPassword(
        password: String,
        hashedPassword: String,
    ): Boolean
}
