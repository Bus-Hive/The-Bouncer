package com.trackmybus.theBouncer.features.v1.domain.repository.permission

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.onFailure
import com.trackmybus.theBouncer.core.result.ResultHandler.onSuccess
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import io.ktor.util.logging.Logger
import java.util.UUID

class PermissionRepositoryImpl(
    private val logger: Logger,
    private val permissionDao: PermissionDao,
    private val userPermissionGroupDao: UserPermissionGroupDao,
    private val permissionGroupDao: PermissionGroupDao,
    private val permissionGroupPermissionRepository: PermissionGroupPermissionDao,
) : PermissionRepository {
    override suspend fun getAll(): Result<List<Permission>, RootError> =
        permissionDao
            .getAllPermissions()
            .also {
                it.onSuccess { logger.info("Successfully fetched all permissions") }
                it.onFailure { logger.error("Error fetching all permissions") }
            }

    override suspend fun getById(id: Int): Result<Permission, RootError> =
        permissionDao
            .getPermissionById(id)
            .also {
                it.onSuccess { logger.info("Successfully fetched permission with id: $id") }
                it.onFailure { logger.error("Error fetching permission with id: $id") }
            }

    override suspend fun add(permission: Permission): Result<Permission, RootError> =
        permissionDao
            .addPermission(permission)
            .also {
                it.onSuccess { logger.info("Successfully added permission: ${permission.name}") }
                it.onFailure { logger.error("Error adding permission: ${permission.name}") }
            }

    override suspend fun add(permissions: List<Permission>): Result<Unit, RootError> =
        permissionDao
            .addPermissions(permissions)
            .also {
                it.onSuccess { logger.info("Successfully added permissions: ${permissions.joinToString { it.name }}") }
                it.onFailure {
                    logger.error(
                        "Error adding permissions: ${permissions.joinToString { it.name }}",
                        it,
                    )
                }
            }

    override suspend fun update(permission: Permission): Result<Permission, RootError> =
        permissionDao
            .updatePermission(permission)
            .also {
                it.onSuccess { logger.info("Successfully updated permission: ${permission.name}") }
                it.onFailure { logger.error("Error updating permission: ${permission.name}") }
            }

    override suspend fun deleteById(id: Int): Result<Unit, RootError> =
        permissionDao
            .deletePermission(id)
            .also {
                it.onSuccess { logger.info("Successfully deleted permission with id: $id") }
                it.onFailure { logger.error("Error deleting permission with id: $id") }
            }

    override suspend fun deleteAll(): Result<Unit, RootError> =
        permissionDao
            .deleteAllPermissions()
            .also {
                it.onSuccess { logger.info("Successfully deleted all permissions") }
                it.onFailure { logger.error("Error deleting all permissions") }
            }

    override suspend fun getPermissionsByUserId(userId: UUID): Result<List<Permission>, RootError> {
        val userPermissionsGroups = userPermissionGroupDao.getUserPermissionGroupsByUserId(userId)

        userPermissionsGroups.onFailure {
            logger.error("Error fetching permission groups for user with id: $userId")
            return Result.Error(it)
        }

        val permissionGroups =
            userPermissionsGroups
                .getDataOrNull()
                ?.mapNotNull {
                    it.permissionGroupId?.let { permissionGroupId ->
                        permissionGroupDao.getPermissionGroup(permissionGroupId).getDataOrNull()
                    }
                }.orEmpty()

        val permissions =
            permissionGroups.flatMap { permissionGroup ->
                permissionGroupPermissionRepository
                    .getPermissionGroupPermissions(permissionGroup.id)
                    .getDataOrNull()
                    ?.mapNotNull { permissionDao.getPermissionById(it).getDataOrNull() }
                    ?: emptyList()
            }

        return Result.Success(permissions)
    }
}
