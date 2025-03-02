package com.trackmybus.theBouncer.features.v1.data.local.dao.permissionGroup

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.addMessage
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.local.entity.PermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.local.mapper.PermissionGroupEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionGroupsTable
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime

class PermissionGroupDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionGroupDao {
    override suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity
                    .new {
                        name = permissionGroup.name
                        description = permissionGroup.description
                        isBaseUserGroup = permissionGroup.isBaseUserGroup
                        createdAt = Clock.System.now().toLocalDateTime(UTC)
                    }.toModel()
            }.addMessage(
                success = "Permission group added successfully",
                failure = "Failed to add permission group",
            ).onFailure {
                logger.error("Failed to add permission group")
            }

    override suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity.findById(permissionGroupId)?.toModel()
            }.addMessage(
                success = "Permission group retrieved successfully",
                failure = "Failed to get permission group",
            ).onFailure {
                logger.error("Failed to get permission group")
            }

    override suspend fun getPermissionGroups(): Result<List<PermissionGroup>, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity.all().map { it.toModel() }
            }.addMessage(
                success = "Permission groups retrieved successfully",
                failure = "Failed to get permission groups",
            ).onFailure {
                logger.error("Failed to get permission groups")
            }

    override suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity
                    .find { PermissionGroupsTable.isBaseUserGroup eq true }
                    .map { it.toModel() }
            }.addMessage(
                success = "Base permission groups retrieved successfully",
                failure = "Failed to get base permission groups",
            ).onFailure {
                logger.error("Failed to get base permission groups")
            }

    override suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity
                    .findByIdAndUpdate(permissionGroup.id) {
                        it.name = permissionGroup.name
                        it.description = permissionGroup.description
                        it.isBaseUserGroup = permissionGroup.isBaseUserGroup
                    }?.toModel()
            }.addMessage(
                success = "Permission group updated successfully",
                failure = "Failed to update permission group",
            ).onFailure {
                logger.error("Failed to update permission group")
            }

    override suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupEntity[permissionGroupId].delete()
                true
            }.addMessage(
                success = "Permission group deleted successfully",
                failure = "Failed to delete permission group",
            ).onFailure {
                logger.error("Failed to delete permission group")
            }
}
