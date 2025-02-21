package com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.UserPermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toEntities
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.updateModel
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.data.tables.UsersPermissionGroupTable.userId
import io.ktor.util.logging.Logger
import java.util.UUID

class UserPermissionGroupDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : UserPermissionGroupDao {
    override suspend fun getAllUserPermissionGroups(): Result<List<UserPermissionGroupEntity>> =
        runCatching {
            dbFactory.dbQuery {
                UserPermissionGroupEntity.all().toList()
            }
        }.onFailure {
            logger.error("Error getting all user permissions", it)
        }

    override suspend fun getUserPermissionGroupsByUserId(id: UUID): Result<List<UserPermissionGroupEntity>> =
        runCatching {
            dbFactory.dbQuery {
                UserPermissionGroupEntity.find { userId eq id }.toList()
            }
        }.onFailure {
            logger.error("Error getting user permission by id: $id", it)
        }

    override suspend fun addUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        runCatching {
            userPermissionGroup.toEntity()
        }.onFailure {
            logger.error("Error adding user permission: ${userPermissionGroup.userId}, ${userPermissionGroup.permissionGroupId}", it)
        }

    override suspend fun addUserPermissionGroups(userPermissionGroups: List<UserPermissionGroup>): Result<Unit> =
        runCatching {
            userPermissionGroups.toEntities()
        }.onFailure {
            logger.error("Error adding user permissions: $userPermissionGroups", it)
        }

    override suspend fun updateUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                UserPermissionGroupEntity
                    .find { userId eq userPermissionGroup.userId }
                    .toList()
                    .find { it.permissionGroupId.value == userPermissionGroup.permissionGroupId }
                    ?.apply {
                        this.updateModel(userPermissionGroup)
                    }
            }
            Unit
        }.onFailure {
            logger.error("Error updating user permission: ${userPermissionGroup.userId}, ${userPermissionGroup.permissionGroupId}", it)
        }

    override suspend fun deleteUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                UserPermissionGroupEntity
                    .find { userId eq userPermissionGroup.userId }
                    .toList()
                    .find { it.permissionGroupId.value == userPermissionGroup.permissionGroupId }
                    ?.delete()
            }
            Unit
        }.onFailure {
            logger.error("Error deleting user permission by id: ${userPermissionGroup.permissionGroupId}", it)
        }

    override suspend fun deleteAllUserPermissionGroups(): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                UserPermissionGroupEntity.all().forEach { it.delete() }
            }
            Unit
        }.onFailure {
            logger.error("Error deleting all user permissions", it)
        }
}
