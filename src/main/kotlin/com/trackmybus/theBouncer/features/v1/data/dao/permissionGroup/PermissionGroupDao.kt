package com.trackmybus.theBouncer.features.v1.data.dao.permissionGroup

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup

interface PermissionGroupDao {
    suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup, RootError>

    suspend fun getPermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError>
}
