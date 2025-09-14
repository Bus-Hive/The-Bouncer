package app.bushive.theBouncer.features.v1.domain.repository.permission

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.Permission
import java.util.UUID

interface PermissionRepository {
    suspend fun getAll(): Result<List<Permission>, RootError>

    suspend fun getById(id: Int): Result<Permission, RootError>

    suspend fun add(permission: Permission): Result<Permission, RootError>

    suspend fun update(permission: Permission): Result<Permission, RootError>

    suspend fun deleteById(id: Int): Result<Unit, RootError>

    suspend fun deleteAll(): Result<Unit, RootError>

    suspend fun getPermissionsByUserId(userId: UUID): Result<List<Permission>, RootError>
}
