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
    ): com.trackmybus.theBouncer.core.result.Result<PermissionGroupDto, RootError>

    suspend fun removePermissionGroup(permissionGroupId: Int): com.trackmybus.theBouncer.core.result.Result<Boolean, RootError>

    suspend fun updatePermissionGroup(
        permissionGroupId: Int,
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): com.trackmybus.theBouncer.core.result.Result<PermissionGroupDto, RootError>

    suspend fun assignPermissionToGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): com.trackmybus.theBouncer.core.result.Result<PermissionGroupPermissionDto, RootError>

    suspend fun removePermissionFromGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): com.trackmybus.theBouncer.core.result.Result<Unit, RootError>

    suspend fun getPermissionGroup(permissionGroupId: Int): com.trackmybus.theBouncer.core.result.Result<PermissionGroupDto, RootError>

    suspend fun getPermissionGroups(): com.trackmybus.theBouncer.core.result.Result<List<PermissionGroupDto>, RootError>

    suspend fun getBasePermissionGroups(): com.trackmybus.theBouncer.core.result.Result<List<PermissionGroupDto>, RootError>

    suspend fun getPermissionGroupsByUserId(
        userId: String,
    ): com.trackmybus.theBouncer.core.result.Result<List<PermissionGroupDto>, RootError>

    suspend fun assignUserToGroup(
        userId: String,
        permissionGroupId: Int,
    ): com.trackmybus.theBouncer.core.result.Result<UserPermissionGroupDto, RootError>

    suspend fun removeUserFromGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<Unit, RootError>
}
