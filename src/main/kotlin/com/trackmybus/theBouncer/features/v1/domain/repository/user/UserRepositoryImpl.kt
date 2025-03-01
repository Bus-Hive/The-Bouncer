package com.trackmybus.theBouncer.features.v1.domain.repository.user

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.onSuccess
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.dao.user.UserDao
import com.trackmybus.theBouncer.features.v1.data.model.User
import io.ktor.util.logging.Logger
import java.util.UUID

class UserRepositoryImpl(
    private val logger: Logger,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun getAll(): Result<List<User>, RootError> =
        userDao.getAllUsers().mapResult { it }.also { result ->
            result.onSuccess { logger.info("Successfully fetched all users") }
            result.onFailure { logger.error("Error fetching all users") }
        }

    override suspend fun getById(id: UUID): Result<User, RootError> =
        userDao.getUserById(id).also { result ->
            result.onSuccess { logger.info("Successfully fetched user with id: $id") }
            result.onFailure { logger.error("Error fetching user with id: $id") }
        }

    override suspend fun getByEmail(email: String): Result<User, RootError> =
        userDao.getUserByEmail(email).also { result ->
            result.onSuccess { logger.info("Successfully fetched user with email: $email") }
            result.onFailure { logger.error("Error fetching user with email: $email") }
        }

    override suspend fun isEmailUnique(email: String): Result<Boolean, RootError> =
        userDao.isEmailUnique(email).also { result ->
            result.onSuccess { logger.info("Email $email is unique") }
            result.onFailure { logger.error("Email $email is not unique") }
        }

    override suspend fun add(user: User): Result<User, RootError> =
        userDao.addUser(user).also { result ->
            result.onSuccess { logger.info("Successfully added user with id: ${user.id}") }
            result.onFailure { logger.error("Error adding user with id: ${user.id}") }
        }

    override suspend fun add(users: List<User>): Result<Unit, RootError> =
        userDao
            .addUsers(users)
            .also { result ->
                result.onSuccess { logger.info("Successfully added users: ${users.joinToString { it.id.toString() }}") }
                result.onFailure { logger.error("Error adding users: ${users.joinToString { it.id.toString() }}") }
            }

    override suspend fun update(user: User): Result<User, RootError> =
        userDao.updateUser(user).also { result ->
            result.onSuccess { logger.info("Successfully updated user with id: ${user.id}") }
            result.onFailure { logger.error("Error updating user with id: ${user.id}") }
        }

    override suspend fun delete(id: UUID): Result<Unit, RootError> =
        userDao.deleteUser(id).also { result ->
            result.onSuccess { logger.info("Successfully deleted user with id: $id") }
            result.onFailure { logger.error("Error deleting user with id: $id") }
        }

    override suspend fun deleteAll(): Result<Unit, RootError> =
        userDao.deleteAllUsers().also { result ->
            result.onSuccess { logger.info("Successfully deleted all users") }
            result.onFailure { logger.error("Error deleting all users") }
        }
}
