package com.trackmybus.theBouncer.features.v1.domain.repository.user

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.mapper.UserEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.User
import io.ktor.util.logging.Logger
import java.util.UUID

class UserRepositoryImpl(
    private val logger: Logger,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun getAll(): Result<List<User>> =
        userDao.getAllUsers().mapResult { it.toModel() }.also { result ->
            result.onSuccess { logger.info("Successfully fetched all users") }
            result.onFailure { logger.error("Error fetching all users", it) }
        }

    override suspend fun getById(id: UUID): Result<User?> =
        userDao.getUserById(id).mapResult { it?.toModel() }.also { result ->
            result.onSuccess { logger.info("Successfully fetched user with id: $id") }
            result.onFailure { logger.error("Error fetching user with id: $id", it) }
        }

    override suspend fun getByEmail(email: String): Result<User?> =
        userDao.getUserByEmail(email).mapResult { it?.toModel() }.also { result ->
            result.onSuccess { logger.info("Successfully fetched user with email: $email") }
            result.onFailure { logger.error("Error fetching user with email: $email", it) }
        }

    override suspend fun isEmailUnique(email: String): Result<Boolean> =
        userDao.isEmailUnique(email).also { result ->
            result.onSuccess { logger.info("Email $email is unique") }
            result.onFailure { logger.error("Email $email is not unique", it) }
        }

    override suspend fun add(user: User): Result<UUID> =
        userDao.addUser(user).also { result ->
            result.onSuccess { logger.info("Successfully added user with id: ${user.id}") }
            result.onFailure { logger.error("Error adding user with id: ${user.id}", it) }
        }

    override suspend fun add(users: List<User>): Result<Unit> =
        userDao
            .addUsers(users)
            .also { result ->
                result.onSuccess { logger.info("Successfully added users: ${users.joinToString { it.id.toString() }}") }
                result.onFailure { logger.error("Error adding users: ${users.joinToString { it.id.toString() }}", it) }
            }

    override suspend fun update(user: User): Result<Unit> =
        userDao.updateUser(user).also { result ->
            result.onSuccess { logger.info("Successfully updated user with id: ${user.id}") }
            result.onFailure { logger.error("Error updating user with id: ${user.id}", it) }
        }

    override suspend fun delete(id: UUID): Result<Unit> =
        userDao.deleteUser(id).also { result ->
            result.onSuccess { logger.info("Successfully deleted user with id: $id") }
            result.onFailure { logger.error("Error deleting user with id: $id", it) }
        }

    override suspend fun deleteAll(): Result<Unit> =
        userDao.deleteAllUsers().also { result ->
            result.onSuccess { logger.info("Successfully deleted all users") }
            result.onFailure { logger.error("Error deleting all users", it) }
        }
}
