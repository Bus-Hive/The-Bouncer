package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.PermissionGroupEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionGroupsTable
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

class PermissionGroupDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionGroupDao {
    override suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity.new {
                        name = permissionGroup.name
                        description = permissionGroup.description
                        isBaseUserGroup = permissionGroup.isBaseUserGroup
                        createdAt = Clock.System.now().toLocalDateTime(UTC)
                    }
                }.toModel()
        }.onFailure {
            logger.error("Failed to add permission group", it)
            Exception("Failed to add permission group", it)
        }

    override suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup?> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity.findById(permissionGroupId)?.toModel()
                }
        }.onFailure {
            logger.error("Failed to get permission group", it)
            Exception("Failed to get permission group", it)
        }

    override suspend fun getPermissionGroups(): Result<List<PermissionGroup>> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity.all().map { it.toModel() }
                }
        }.onFailure {
            logger.error("Failed to get permission groups", it)
            Exception("Failed to get permission groups", it)
        }

    override suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity
                        .find { PermissionGroupsTable.isBaseUserGroup eq true }
                        .map { it.toModel() }
                }
        }.onFailure {
            logger.error("Failed to get base permission groups", it)
            Exception("Failed to get base permission groups", it)
        }

    override suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity[permissionGroup.id].apply {
                        name = permissionGroup.name
                        description = permissionGroup.description
                        isBaseUserGroup = permissionGroup.isBaseUserGroup
                    }
                }.toModel()
        }.onFailure {
            logger.error("Failed to update permission group", it)
            Exception("Failed to update permission group", it)
        }

    override suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupEntity[permissionGroupId].delete()
                    true
                }
        }.onFailure {
            logger.error("Failed to delete permission group", it)
            Exception("Failed to delete permission group", it)
        }
}
