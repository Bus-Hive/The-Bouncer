package com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.addMessage
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.core.result.errors.ValidationError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.UserPermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersPermissionGroupTable.userId
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
import io.ktor.util.logging.Logger
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class UserPermissionGroupDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : UserPermissionGroupDao {
    override suspend fun getAllUserPermissionGroups(): Result<List<UserPermissionGroup>, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity.all().toList().toModel()
            }.addMessage(
                success = "Successfully fetched all user permissions",
                failure = "Error fetching all user permissions",
            ).onFailure {
                logger.error("Error getting all user permissions")
            }

    override suspend fun getUserPermissionGroupsByUserId(id: UUID): Result<List<UserPermissionGroup>, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity.find { userId eq id }.toList().toModel()
            }.addMessage(
                success = "Successfully fetched user permissions by id: $id",
                failure = "Error fetching user permissions by id: $id",
            ).onFailure {
                logger.error("Error getting user permission by id: $id")
            }

    override suspend fun addUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity
                    .new {
                        require(userPermissionGroup.userId != null && userPermissionGroup.permissionGroupId != null) {
                            Result.Error(error = ValidationError.EmptyField, data = userPermissionGroup)
                        }
                        this.userId = EntityID(userPermissionGroup.userId, UsersTable)
                        this.permissionGroupId = EntityID(userPermissionGroup.permissionGroupId, PermissionsTable)
                    }.toModel()
            }.addMessage(
                success = "User permission added successfully",
                failure = "Failed to add user permission",
            ).onFailure {
                logger.error(
                    "Error adding user permission: ${userPermissionGroup.userId}, ${userPermissionGroup.permissionGroupId}",
                )
            }

    override suspend fun addUserPermissionGroups(userPermissionGroups: List<UserPermissionGroup>): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                userPermissionGroups.forEach {
                    require(it.userId != null) {
                        "Cannot create entity with null userId"
                    }

                    require(it.permissionGroupId != null) {
                        "Cannot create entity with null permissionId"
                    }

                    UserPermissionGroupEntity.new {
                        this.userId = EntityID(it.userId, UsersTable)
                        this.permissionGroupId = EntityID(it.permissionGroupId, PermissionsTable)
                    }
                }
            }.addMessage(
                success = "User permissions added successfully",
                failure = "Failed to add user permissions",
            ).onFailure {
                logger.error("Error adding user permissions: $userPermissionGroups")
            }

    override suspend fun updateUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity
                    .find { userId eq userPermissionGroup.userId }
                    .toList()
                    .find { it.permissionGroupId.value == userPermissionGroup.permissionGroupId }
                    ?.apply {
                        require(
                            userPermissionGroup.userId != null &&
                                userPermissionGroup.permissionGroupId != null &&
                                userPermissionGroup.permissionGroupId == this.permissionGroupId.value,
                        ) {
                            Result.Error(error = ValidationError.EmptyField, data = userPermissionGroup)
                        }
                        this.userId = EntityID(userPermissionGroup.userId, UsersTable)
                        this.permissionGroupId = EntityID(userPermissionGroup.permissionGroupId, PermissionsTable)
                    }?.toModel()
            }.addMessage(
                success = "User permission updated successfully",
                failure = "Failed to update user permission",
            ).onFailure {
                logger.error(
                    "Error updating user permission: ${userPermissionGroup.userId}, ${userPermissionGroup.permissionGroupId}",
                )
            }

    override suspend fun deleteUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity
                    .find { userId eq userPermissionGroup.userId }
                    .toList()
                    .find { it.permissionGroupId.value == userPermissionGroup.permissionGroupId }
                    ?.delete()
            }.addMessage(
                success = "User permission deleted successfully",
                failure = "Failed to delete user permission",
            ).onFailure {
                logger.error(
                    "Error deleting user permission: ${userPermissionGroup.userId}, ${userPermissionGroup.permissionGroupId}",
                )
            }

    override suspend fun deleteAllUserPermissionGroups(): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                UserPermissionGroupEntity.all().forEach { it.delete() }
            }.addMessage(
                success = "All user permissions deleted successfully",
                failure = "Failed to delete all user permissions",
            ).onFailure {
                logger.error("Error deleting all user permissions")
            }
}
