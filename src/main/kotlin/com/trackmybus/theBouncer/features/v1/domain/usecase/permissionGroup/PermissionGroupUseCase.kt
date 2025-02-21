package com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup

import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupDto

interface PermissionGroupUseCase {
    suspend fun addPermissionGroup(
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto>

    suspend fun removePermissionGroup(permissionGroupId: Int): Result<Boolean>

    suspend fun updatePermissionGroup(
        permissionGroupId: Int,
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto>

    suspend fun assignPermissionToGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<Boolean>

    suspend fun removePermissionFromGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<Boolean>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroupDto?>

    suspend fun getPermissionGroups(): Result<List<PermissionGroupDto>>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroupDto>>

    suspend fun getPermissionGroupsByUserId(userId: String): Result<List<PermissionGroupDto>>

    suspend fun assignUserToGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<Unit>

    suspend fun removeUserFromGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<Unit>
}
