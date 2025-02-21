package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroupPermission

interface PermissionGroupPermissionDao {
    suspend fun addPermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<PermissionGroupPermission, RootError>

    suspend fun getPermissionGroupPermissions(permissionGroupId: Int): Result<List<Int>, RootError>

    suspend fun deletePermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Unit, RootError>

    suspend fun deletePermissionGroupPermissions(permissionGroupId: Int): Result<Unit, RootError>

    suspend fun deletePermissionPermissions(permissionId: Int): Result<Unit, RootError>
}
