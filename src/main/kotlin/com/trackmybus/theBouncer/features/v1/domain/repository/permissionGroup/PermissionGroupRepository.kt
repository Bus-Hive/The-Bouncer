package com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup

import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import java.util.UUID

interface PermissionGroupRepository {
    suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup?>

    suspend fun getPermissionGroups(): Result<List<PermissionGroup>>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>>

    suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup>

    suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean>

    suspend fun getPermissionGroupsByUserId(userId: UUID): Result<List<PermissionGroup>>

    suspend fun addPermissionToGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean>

    suspend fun removePermissionFromGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean>
}
