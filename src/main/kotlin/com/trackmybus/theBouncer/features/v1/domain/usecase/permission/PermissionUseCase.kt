package com.trackmybus.theBouncer.features.v1.domain.usecase.permission

import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionDto

interface PermissionUseCase {
    suspend fun addPermission(
        name: String,
        description: String,
        permission: String,
    ): Result<Int>

    suspend fun removePermission(permissionId: Int): Result<Unit>

    suspend fun updatePermission(
        permissionId: Int,
        name: String,
        description: String,
        permission: String,
    ): Result<Unit>

    suspend fun getPermission(permissionId: Int): Result<PermissionDto?>

    suspend fun getPermissions(): Result<List<PermissionDto>>

    suspend fun getPermissionsForUser(userId: String): Result<List<PermissionDto>>
}
