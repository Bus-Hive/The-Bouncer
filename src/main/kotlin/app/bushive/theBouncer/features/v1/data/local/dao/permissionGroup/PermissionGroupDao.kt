package app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroup

interface PermissionGroupDao {
    suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup, RootError>

    suspend fun getPermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError>
}
