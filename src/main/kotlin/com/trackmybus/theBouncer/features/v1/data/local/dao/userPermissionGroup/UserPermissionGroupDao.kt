package com.trackmybus.theBouncer.features.v1.data.local.dao.userPermissionGroup

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup
import java.util.UUID

interface UserPermissionGroupDao {
    suspend fun getAllUserPermissionGroups(): Result<List<UserPermissionGroup>, RootError>

    suspend fun getUserPermissionGroupsByUserId(id: UUID): Result<List<UserPermissionGroup>, RootError>

    suspend fun addUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError>

    suspend fun addUserPermissionGroups(userPermissionGroups: List<UserPermissionGroup>): Result<Unit, RootError>

    suspend fun updateUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError>

    suspend fun deleteUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit, RootError>

    suspend fun deleteAllUserPermissionGroups(): Result<Unit, RootError>
}
