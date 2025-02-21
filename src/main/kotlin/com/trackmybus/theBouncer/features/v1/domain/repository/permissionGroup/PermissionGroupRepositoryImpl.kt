package com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup

import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup.PermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission.PermissionGroupPermissionDao
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import io.ktor.util.logging.Logger
import java.util.UUID

class PermissionGroupRepositoryImpl(
    private val logger: Logger,
    private val permissionGroupDao: PermissionGroupDao,
    private val permissionGroupPermissionDao: PermissionGroupPermissionDao,
    private val userPermissionGroupDao: UserPermissionGroupDao,
) : PermissionGroupRepository {
    override suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup> =
        permissionGroupDao.addPermissionGroup(permissionGroup)

    override suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup?> =
        permissionGroupDao.getPermissionGroup(permissionGroupId)

    override suspend fun getPermissionGroups(): Result<List<PermissionGroup>> = permissionGroupDao.getPermissionGroups()

    override suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>> = permissionGroupDao.getBasePermissionGroups()

    override suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup> =
        permissionGroupDao.updatePermissionGroup(permissionGroup)

    override suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean> =
        permissionGroupDao.deletePermissionGroup(permissionGroupId)

    override suspend fun getPermissionGroupsByUserId(userId: UUID): Result<List<PermissionGroup>> =
        userPermissionGroupDao.getUserPermissionGroupsByUserId(userId).map {
            it.toModel().map {
                it.permissionGroupId
                    ?.let { permissionGroupId -> permissionGroupDao.getPermissionGroup(permissionGroupId) }
                    ?.getOrNull() ?: throw Exception("Permission group not found")
            }
        }

    override suspend fun addPermissionToGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean> = permissionGroupPermissionDao.addPermissionGroupPermission(permissionGroupId, permissionId)

    override suspend fun removePermissionFromGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean> = permissionGroupPermissionDao.deletePermissionGroupPermission(permissionGroupId, permissionId)
}
