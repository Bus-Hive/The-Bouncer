package com.trackmybus.theBouncer.features.v1.domain.usecase.permission

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionDto

interface PermissionUseCase {
    suspend fun addPermission(
        name: String,
        description: String,
        permission: String,
    ): Result<PermissionDto, RootError>

    suspend fun removePermission(permissionId: Int): Result<Unit, RootError>

    suspend fun updatePermission(
        permissionId: Int,
        name: String,
        description: String,
        permission: String,
    ): Result<PermissionDto, RootError>

    suspend fun getPermission(permissionId: Int): Result<PermissionDto, RootError>

    suspend fun getPermissions(): Result<List<PermissionDto>, RootError>

    suspend fun getPermissionsForUser(userId: String): Result<List<PermissionDto>, RootError>
}
