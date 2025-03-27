package app.bushive.theBouncer.features.v1.data.local.dao.permission

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.Permission

interface PermissionDao {
    suspend fun getAllPermissions(): Result<List<Permission>, RootError>

    suspend fun getPermissionById(id: Int): Result<Permission, RootError>

    suspend fun addPermission(permission: Permission): Result<Permission, RootError>

    suspend fun updatePermission(permission: Permission): Result<Permission, RootError>

    suspend fun deletePermission(id: Int): Result<Unit, RootError>

    suspend fun deleteAllPermissions(): Result<Unit, RootError>
}
