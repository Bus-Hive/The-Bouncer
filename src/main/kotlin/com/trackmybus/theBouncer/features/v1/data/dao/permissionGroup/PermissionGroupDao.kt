package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup

import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup

interface PermissionGroupDao {
    suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup?>

    suspend fun getPermissionGroups(): Result<List<PermissionGroup>>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>>

    suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup>

    suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean>
}
