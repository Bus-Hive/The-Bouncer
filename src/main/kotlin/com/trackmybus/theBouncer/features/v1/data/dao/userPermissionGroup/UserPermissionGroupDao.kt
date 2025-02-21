package com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup

import com.trackmybus.theBouncer.features.v1.data.entity.UserPermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import java.util.UUID

interface UserPermissionGroupDao {
    suspend fun getAllUserPermissionGroups(): Result<List<UserPermissionGroupEntity>>

    suspend fun getUserPermissionGroupsByUserId(id: UUID): Result<List<UserPermissionGroupEntity>>

    suspend fun addUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun addUserPermissionGroups(userPermissionGroups: List<UserPermissionGroup>): Result<Unit>

    suspend fun updateUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun deleteUserPermissionGroup(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun deleteAllUserPermissionGroups(): Result<Unit>
}
