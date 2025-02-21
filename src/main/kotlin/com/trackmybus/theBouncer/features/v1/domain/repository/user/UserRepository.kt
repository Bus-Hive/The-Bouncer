package com.trackmybus.theBouncer.features.v1.domain.repository.user

import com.trackmybus.theBouncer.features.v1.data.model.User
import java.util.UUID

interface UserRepository {
    suspend fun getAll(): Result<List<User>>

    suspend fun getById(id: UUID): Result<User?>

    suspend fun getByEmail(email: String): Result<User?>

    suspend fun isEmailUnique(email: String): Result<Boolean>

    suspend fun add(user: User): Result<UUID>

    suspend fun add(users: List<User>): Result<Unit>

    suspend fun update(user: User): Result<Unit>

    suspend fun delete(id: UUID): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
