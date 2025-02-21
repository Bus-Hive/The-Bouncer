package com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup

import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import java.util.UUID

interface UserPermissionGroupRepository {
    suspend fun getAll(): Result<List<UserPermissionGroup>>

    suspend fun getByUserId(id: UUID): Result<List<UserPermissionGroup>>

    suspend fun add(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun add(userPermissionGroups: List<UserPermissionGroup>): Result<Unit>

    suspend fun update(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun deleteById(userPermissionGroup: UserPermissionGroup): Result<Unit>

    suspend fun deleteAll(): Result<Unit>
}
