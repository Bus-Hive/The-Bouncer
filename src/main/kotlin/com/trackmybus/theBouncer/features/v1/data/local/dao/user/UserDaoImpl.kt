package com.trackmybus.theBouncer.features.v1.data.local.dao.user

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.addMessage
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.ValidationError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.local.entity.UserEntity
import com.trackmybus.theBouncer.features.v1.data.local.mapper.UserEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.local.model.User
import com.trackmybus.theBouncer.features.v1.data.local.tables.UsersTable
import io.ktor.util.logging.Logger
import java.util.UUID

class UserDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : UserDao {
    override suspend fun getAllUsers(): Result<List<User>, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.all().toList().toModel()
            }.addMessage(
                success = "Successfully fetched all users",
                failure = "Error fetching all users",
            )

    override suspend fun getUserById(id: UUID): Result<User, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.findById(id)?.toModel()
            }.addMessage(
                success = "Successfully fetched user with id: $id",
                failure = "Error fetching user with id: $id",
            )

    override suspend fun getUserByEmail(email: String): Result<User, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.find { UsersTable.email eq email }.firstOrNull()?.toModel()
            }.addMessage(
                success = "Successfully fetched user by email: $email",
                failure = "Error getting user by email: $email",
            ).onFailure {
                logger.error("Error getting user by email: $email", it)
            }

    override suspend fun addUser(user: User): Result<User, RootError> {
        require(user.id == null && user.createdAt != null && user.provider != null) {
            return Result.Error(
                error = ValidationError.EmptyField,
                data = user,
            )
        }

        return dbFactory
            .dbQuery {
                UserEntity
                    .new {
                        this.email = user.email
                        this.firstName = user.firstName
                        this.lastName = user.lastName
                        this.hashedPassword = user.hashedPassword
                        this.provider = user.provider
                        this.role = user.role
                        this.createdAt = user.createdAt
                    }.toModel()
            }.addMessage(
                success = "Successfully added user with id: ${user.id}",
                failure = "Error adding user with id: ${user.id}",
            )
    }

    override suspend fun addUsers(users: List<User>): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                users.forEach { user ->
                    require(user.id == null && user.createdAt != null && user.provider != null) {
                        Result.Error(
                            error = ValidationError.EmptyField,
                            data = user,
                        )
                    }
                    UserEntity.new {
                        this.email = user.email
                        this.firstName = user.firstName
                        this.lastName = user.lastName
                        this.hashedPassword = user.hashedPassword
                        this.provider = user.provider
                        this.role = user.role
                        this.createdAt = user.createdAt
                    }
                }
            }.addMessage(
                success = "Successfully added users: ${users.joinToString { it.id.toString() }}",
                failure = "Error adding users: ${users.joinToString { it.id.toString() }}",
            )

    override suspend fun updateUser(user: User): Result<User, RootError> =
        dbFactory
            .dbQuery {
                require(user.id != null && user.createdAt != null && user.provider != null) {
                    Result.Error(
                        error = ValidationError.EmptyField,
                        data = user,
                    )
                }
                user.id
                    ?.let { userId ->
                        UserEntity.findByIdAndUpdate(userId) {
                            it.email = user.email
                            it.firstName = user.firstName
                            it.lastName = user.lastName
                            it.hashedPassword = user.hashedPassword
                            it.provider = user.provider
                            it.role = user.role
                            it.createdAt = user.createdAt
                        }
                    }?.toModel()
            }.addMessage(
                success = "Successfully updated user with id: ${user.id}",
                failure = "Error updating user with id: ${user.id}",
            )

    override suspend fun deleteUser(id: UUID): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.findById(id)?.delete()
            }.onFailure {
                logger.error("Error deleting user: $id")
            }

    override suspend fun deleteAllUsers(): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.all().forEach { it.delete() }
            }.addMessage(
                success = "Successfully deleted all users",
                failure = "Error deleting all users",
            ).onFailure {
                logger.error("Error deleting all users")
            }

    override suspend fun isEmailUnique(email: String): Result<Boolean, RootError> =
        dbFactory
            .dbQuery {
                UserEntity.find { UsersTable.email eq email }.empty()
            }.addMessage(
                success = "Email $email is unique",
                failure = "Email $email is not unique",
            ).onFailure {
                logger.error("Error checking if email is unique: $email", it)
            }
}
