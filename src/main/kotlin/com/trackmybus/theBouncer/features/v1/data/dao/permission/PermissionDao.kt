package com.trackmybus.theBouncer.features.v1.data.dao.permission

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.model.Permission

interface PermissionDao {
    suspend fun getAllPermissions(): Result<List<Permission>, RootError>

    suspend fun getPermissionById(id: Int): Result<Permission, RootError>

    suspend fun addPermission(permission: Permission): Result<Permission, RootError>

    suspend fun addPermissions(permissions: List<Permission>): Result<Unit, RootError>

    suspend fun updatePermission(permission: Permission): Result<Permission, RootError>

    suspend fun deletePermission(id: Int): Result<Unit, RootError>

    suspend fun deleteAllPermissions(): Result<Unit, RootError>
}
