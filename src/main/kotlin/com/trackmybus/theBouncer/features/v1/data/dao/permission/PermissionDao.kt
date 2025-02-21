package com.trackmybus.theBouncer.features.v1.data.dao.permission

import com.trackmybus.theBouncer.features.v1.data.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Permission

interface PermissionDao {
    suspend fun getAllPermissions(): Result<List<PermissionEntity>>

    suspend fun getPermissionById(id: Int): Result<PermissionEntity?>

    suspend fun addPermission(permission: Permission): Result<Int>

    suspend fun addPermissions(permissions: List<Permission>): Result<Unit>

    suspend fun updatePermission(permission: Permission): Result<Unit>

    suspend fun deletePermission(id: Int): Result<Unit>

    suspend fun deleteAllPermissions(): Result<Unit>
}
