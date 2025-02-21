package com.trackmybus.theBouncer.features.v1.data.dao.permission

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.mapper.PermissionEntityMapper.toEntities
import com.trackmybus.theBouncer.features.v1.data.mapper.PermissionEntityMapper.toEntity
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import io.ktor.util.logging.Logger

class PermissionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionDao {
    override suspend fun getAllPermissions(): Result<List<PermissionEntity>> =
        runCatching {
            dbFactory.dbQuery {
                PermissionEntity.all().toList()
            }
        }.onFailure {
            logger.error("Error getting all permissions", it)
        }

    override suspend fun getPermissionById(id: Int): Result<PermissionEntity?> =
        runCatching {
            dbFactory.dbQuery {
                PermissionEntity.findById(id.toInt())
            }
        }.onFailure {
            logger.error("Error getting permission by id: $id", it)
        }

    override suspend fun addPermission(permission: Permission): Result<Int> =
        runCatching {
            permission.toEntity()
        }.onFailure {
            logger.error("Error adding permission: ${permission.name}, ${permission.description}", it)
        }

    override suspend fun addPermissions(permissions: List<Permission>): Result<Unit> =
        runCatching {
            permissions.toEntities()
        }.onFailure {
            logger.error("Error adding permissions: $permissions", it)
        }

    override suspend fun updatePermission(permission: Permission): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                permission.id?.let {
                    PermissionEntity.findByIdAndUpdate(it) {
                        it.name = permission.name
                        it.description = permission.description
                        it.name = permission.name
                    }
                }
            }
            Unit
        }.onFailure {
            logger.error(
                "Error updating permission: ${permission.id}, ${permission.name}, ${permission.description}",
                it,
            )
        }

    override suspend fun deletePermission(id: Int): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                PermissionEntity.findById(id)?.delete()
            }
            Unit
        }.onFailure {
            logger.error("Error deleting permission by id: $id", it)
        }

    override suspend fun deleteAllPermissions(): Result<Unit> =
        runCatching {
            dbFactory.dbQuery {
                PermissionEntity.all().forEach { it.delete() }
            }
            Unit
        }.onFailure {
            logger.error("Error deleting all permissions", it)
        }
}
