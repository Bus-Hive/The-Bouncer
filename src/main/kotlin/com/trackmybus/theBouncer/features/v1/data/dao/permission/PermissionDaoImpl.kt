package com.trackmybus.theBouncer.features.v1.data.dao.permission

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.addMessage
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.PermissionEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import io.ktor.util.logging.Logger

class PermissionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionDao {
    override suspend fun getAllPermissions(): Result<List<Permission>, RootError> =
        dbFactory
            .dbQuery {
                PermissionEntity.all().toList().toModel()
            }.addMessage(
                success = "Successfully fetched all permissions",
                failure = "Error fetching all permissions",
            ).onFailure {
                logger.error("Error getting all permissions")
            }

    override suspend fun getPermissionById(id: Int): Result<Permission, RootError> =
        dbFactory
            .dbQuery {
                PermissionEntity.findById(id.toInt())?.toModel()
            }.addMessage(
                success = "Successfully fetched permission by id: $id",
                failure = "Error getting permission by id: $id",
            ).onFailure {
                logger.error("Error getting permission by id: $id")
            }

    override suspend fun addPermission(permission: Permission): Result<Permission, RootError> =
        dbFactory
            .dbQuery {
                PermissionEntity
                    .new {
                        this.name = permission.name
                        this.description = permission.description
                        this.permission = permission.permission
                        this.createdAt = permission.createdAt
                    }.toModel()
            }.addMessage(
                success = "Successfully added permission: ${permission.name}, ${permission.description}",
                failure = "Error adding permission: ${permission.name}, ${permission.description}",
            ).onFailure {
                logger.error("Error adding permission: ${permission.name}, ${permission.description}")
            }

    override suspend fun updatePermission(permission: Permission): Result<Permission, RootError> =
        dbFactory
            .dbQuery {
                permission.id?.let {
                    PermissionEntity
                        .findByIdAndUpdate(it) {
                            it.name = permission.name
                            it.description = permission.description
                            it.name = permission.name
                        }?.toModel()
                }
            }.addMessage(
                success = "Successfully updated permission: ${permission.id}, ${permission.name}, ${permission.description}",
                failure = "Error updating permission: ${permission.id}, ${permission.name}, ${permission.description}",
            ).onFailure {
                logger.error(
                    "Error updating permission: ${permission.id}, ${permission.name}, ${permission.description}",
                    it,
                )
            }

    override suspend fun deletePermission(id: Int): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                PermissionEntity.findById(id)?.delete()
            }.addMessage(
                success = "Successfully deleted permission by id: $id",
                failure = "Error deleting permission by id: $id",
            ).onFailure {
                logger.error("Error deleting permission by id: $id")
            }

    override suspend fun deleteAllPermissions(): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                PermissionEntity.all().forEach { it.delete() }
            }.addMessage(
                success = "Successfully deleted all permissions",
                failure = "Error deleting all permissions",
            ).onFailure {
                logger.error("Error deleting all permissions")
            }
}
