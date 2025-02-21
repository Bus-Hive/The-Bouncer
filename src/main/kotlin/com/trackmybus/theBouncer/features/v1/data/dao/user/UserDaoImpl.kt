package com.trackmybus.theBouncer.features.v1.data.dao.user

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.UserEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.UserEntityMapper.toEntities
import com.trackmybus.theBouncer.features.v1.data.mapper.UserEntityMapper.toEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.UserEntityMapper.updateModel
import com.trackmybus.theBouncer.features.v1.data.model.User
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
import io.ktor.util.logging.Logger
import java.util.UUID

class UserDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : UserDao {
    override suspend fun getAllUsers(): Result<List<UserEntity>> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.all().toList()
            }
        }.onFailure {
            logger.error("Error getting all users", it)
        }

    override suspend fun getUserById(id: UUID): Result<UserEntity?> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.findById(id)
            }
        }.onFailure {
            logger.error("Error getting user by id: $id", it)
        }

    override suspend fun getUserByEmail(email: String): Result<UserEntity?> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.find { UsersTable.email eq email }.firstOrNull()
            }
        }.onFailure {
            logger.error("Error getting user by email: $email", it)
        }

    override suspend fun addUser(user: User): Result<UUID> =
        runCatching {
            user.toEntity()
        }.onFailure {
            logger.error("Error adding user: ${user.firstName}, ${user.email}, ${user.hashedPassword}", it)
        }

    override suspend fun addUsers(users: List<User>): Result<Unit> =
        runCatching {
            users.toEntities()
        }.onFailure {
            logger.error("Error adding users: $users", it)
        }

    override suspend fun updateUser(user: User): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                user.id
                    ?.let {
                        UserEntity.findById(it)
                    }?.apply {
                        this.updateModel(user)
                    }
            }
            Unit
        }.onFailure {
            logger.error("Error updating user: ${user.id}", it)
        }

    override suspend fun deleteUser(id: UUID): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.findById(id)?.delete()
            }
            Unit
        }.onFailure {
            logger.error("Error deleting user: $id", it)
        }

    override suspend fun deleteAllUsers(): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.all().forEach { it.delete() }
            }
            Unit
        }.onFailure {
            logger.error("Error deleting all users", it)
        }

    override suspend fun isEmailUnique(email: String): Result<Boolean> =
        runCatching {
            dbFactory.dbQuery {
                UserEntity.find { UsersTable.email eq email }.empty()
            }
        }.onFailure {
            logger.error("Error checking if email is unique: $email", it)
        }
}
