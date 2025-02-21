package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionGroupPermissionEntity
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionGroupPermissionTable
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionGroupsTable
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionsTable
import io.ktor.util.logging.Logger
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class PermissionGroupPermissionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionGroupPermissionDao {
    override suspend fun addPermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupPermissionEntity.new {
                        this.permissionGroupId = EntityID(permissionGroupId, PermissionGroupsTable)
                        this.permissionId = EntityID(permissionId, PermissionsTable)
                    }
                }
            true
        }.onFailure {
            logger.error("Failed to add permission group permission", it)
            Exception("Failed to add permission group permission", it)
        }

    override suspend fun getPermissionGroupPermissions(permissionGroupId: Int): Result<List<Int>> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupPermissionEntity
                        .find { PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId }
                        .map { it.permissionId.value }
                }
        }.onFailure {
            logger.error("Failed to get permission group permissions", it)
            Exception("Failed to get permission group permissions", it)
        }

    override suspend fun deletePermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupPermissionEntity
                        .find {
                            (PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId).and {
                                PermissionGroupPermissionTable.permissionId eq
                                    permissionId
                            }
                        }.single()
                        .delete()
                }
            true
        }.onFailure {
            logger.error("Failed to delete permission group permission", it)
            Exception("Failed to delete permission group permission", it)
        }

    override suspend fun deletePermissionGroupPermissions(permissionGroupId: Int): Result<Boolean> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupPermissionEntity
                        .find { PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId }
                        .forEach { it.delete() }
                }
            true
        }.onFailure {
            logger.error("Failed to delete permission group permissions", it)
            Exception("Failed to delete permission group permissions", it)
        }

    override suspend fun deletePermissionPermissions(permissionId: Int): Result<Boolean> =
        runCatching {
            dbFactory
                .dbQuery {
                    PermissionGroupPermissionEntity
                        .find { PermissionGroupPermissionTable.permissionId eq permissionId }
                        .forEach { it.delete() }
                }
            true
        }.onFailure {
            logger.error("Failed to delete permission permissions", it)
            Exception("Failed to delete permission permissions", it)
        }
}
