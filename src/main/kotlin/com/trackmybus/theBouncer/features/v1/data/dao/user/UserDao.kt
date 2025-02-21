package com.trackmybus.theBouncer.features.v1.data.dao.user

import com.trackmybus.theBouncer.features.v1.data.entity.UserEntity
import com.trackmybus.theBouncer.features.v1.data.model.User
import java.util.UUID

interface UserDao {
    suspend fun getAllUsers(): Result<List<UserEntity>>

    suspend fun getUserById(id: UUID): Result<UserEntity?>

    suspend fun getUserByEmail(email: String): Result<UserEntity?>

    suspend fun addUser(user: User): Result<UUID>

    suspend fun addUsers(users: List<User>): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun deleteUser(id: UUID): Result<Unit>

    suspend fun deleteAllUsers(): Result<Unit>

    suspend fun isEmailUnique(email: String): Result<Boolean>
}
