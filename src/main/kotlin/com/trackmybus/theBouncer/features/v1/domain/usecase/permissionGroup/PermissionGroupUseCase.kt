package com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupDto
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupPermissionDto
import com.trackmybus.theBouncer.features.v1.domain.dto.UserPermissionGroupDto

interface PermissionGroupUseCase {
    suspend fun addPermissionGroup(
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto, RootError>

    suspend fun removePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError>

    suspend fun updatePermissionGroup(
        permissionGroupId: Int,
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto, RootError>

    suspend fun assignPermissionToGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<PermissionGroupPermissionDto, RootError>

    suspend fun removePermissionFromGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<Unit, RootError>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroupDto, RootError>

    suspend fun getPermissionGroups(): Result<List<PermissionGroupDto>, RootError>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroupDto>, RootError>

    suspend fun getPermissionGroupsByUserId(userId: String): Result<List<PermissionGroupDto>, RootError>

    suspend fun assignUserToGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<UserPermissionGroupDto, RootError>

    suspend fun removeUserFromGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<Unit, RootError>
}
