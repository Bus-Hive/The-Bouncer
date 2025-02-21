package com.trackmybus.theBouncer.features.v1.domain.repository.permission

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.features.v1.data.dao.permission.PermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.mapper.PermissionEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toModel
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
    override suspend fun getAll(): Result<List<Permission>> =
        permissionDao.getAllPermissions().mapResult { it.toModel() }.also {
            it.onSuccess { logger.info("Successfully fetched all permissions") }
            it.onFailure { logger.error("Error fetching all permissions", it) }
        }

    override suspend fun getById(id: Int): Result<Permission?> =
        permissionDao
            .getPermissionById(id)
            .mapResult { it?.toModel() }
            .also {
                it.onSuccess { logger.info("Successfully fetched permission with id: $id") }
                it.onFailure { logger.error("Error fetching permission with id: $id", it) }
            }

    override suspend fun add(permission: Permission): Result<Int> =
        permissionDao
            .addPermission(permission)
            .also {
                it.onSuccess { logger.info("Successfully added permission: ${permission.name}") }
                it.onFailure { logger.error("Error adding permission: ${permission.name}", it) }
            }

    override suspend fun add(permissions: List<Permission>): Result<Unit> =
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

    override suspend fun update(permission: Permission): Result<Unit> =
        permissionDao
            .updatePermission(permission)
            .also {
                it.onSuccess { logger.info("Successfully updated permission: ${permission.name}") }
                it.onFailure { logger.error("Error updating permission: ${permission.name}", it) }
            }

    override suspend fun deleteById(id: Int): Result<Unit> =
        permissionDao
            .deletePermission(id)
            .also {
                it.onSuccess { logger.info("Successfully deleted permission with id: $id") }
                it.onFailure { logger.error("Error deleting permission with id: $id", it) }
            }

    override suspend fun deleteAll(): Result<Unit> =
        permissionDao
            .deleteAllPermissions()
            .also {
                it.onSuccess { logger.info("Successfully deleted all permissions") }
                it.onFailure { logger.error("Error deleting all permissions", it) }
            }

    override suspend fun getPermissionsByUserId(userId: UUID): Result<List<Permission>> {
        val userPermissionsGroups = userPermissionGroupDao.getUserPermissionGroupsByUserId(userId)

        if (userPermissionsGroups.isFailure) {
            return Result.failure(Exception("Error getting user permissions for user with id: $userId"))
        }
        val permissionGroups =
            userPermissionGroupDao.getUserPermissionGroupsByUserId(userId).map {
                it.toModel().map {
                    (
                        it.permissionGroupId
                            ?.let { permissionGroupId -> permissionGroupDao.getPermissionGroup(permissionGroupId) }
                            ?.getOrNull() ?: throw Exception("Permission group not found")
                    )
                }
            }
        logger.info("Successfully fetched permission groups for user with id: $userId")
        logger.info("Permission groups: $permissionGroups")

        val permissions = mutableListOf<Permission>()

        permissionGroups.getOrNull()?.forEach {
            logger.info("Permission group: $it")
            permissionGroupPermissionRepository.getPermissionGroupPermissions(it.id).getOrNull()?.forEach {
                logger.info("Permission id: $it")
                permissionDao.getPermissionById(it).getOrNull()?.let { permission ->
                    permissions.add(permission.toModel())
                }
            }
        }

//        ?.map {
//                    permissionDao.getPermissionById(it.id).map {
//                        it?.toModel() ?: throw Exception("Permission not found")
//                    }
//                }?.map { it.getOrNull() ?: throw Exception("Permission not found") } ?: emptyList()

        return Result.success(permissions)
    }
}
