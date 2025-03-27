package app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroupPermission

interface PermissionGroupPermissionDao {
    suspend fun addPermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<PermissionGroupPermission, RootError>

    suspend fun getPermissionGroupPermissions(permissionGroupId: Int): Result<List<Int>, RootError>

    suspend fun deletePermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Unit, RootError>

    suspend fun deletePermissionGroupPermissions(permissionGroupId: Int): Result<Unit, RootError>

    suspend fun deletePermissionPermissions(permissionId: Int): Result<Unit, RootError>
}
