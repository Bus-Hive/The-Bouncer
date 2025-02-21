package com.trackmybus.theBouncer.features.v1.domain.repository.permission

import com.trackmybus.theBouncer.features.v1.data.model.Permission
import java.util.UUID

interface PermissionRepository {
    suspend fun getAll(): Result<List<Permission>>

    suspend fun getById(id: Int): Result<Permission?>

    suspend fun add(permission: Permission): Result<Int>

    suspend fun add(permissions: List<Permission>): Result<Unit>

    suspend fun update(permission: Permission): Result<Unit>

    suspend fun deleteById(id: Int): Result<Unit>

    suspend fun deleteAll(): Result<Unit>

    suspend fun getPermissionsByUserId(userId: UUID): Result<List<Permission>>
}
