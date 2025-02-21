package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroupPermission

interface PermissionGroupPermissionDao {
    suspend fun addPermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean>

    suspend fun getPermissionGroupPermissions(permissionGroupId: Int): Result<List<Int>>

    suspend fun deletePermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Boolean>

    suspend fun deletePermissionGroupPermissions(permissionGroupId: Int): Result<Boolean>

    suspend fun deletePermissionPermissions(permissionId: Int): Result<Boolean>
}
