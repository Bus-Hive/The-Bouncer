package app.bushive.theBouncer.features.v1.domain.repository.permissionGroup

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroup
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroupPermission
import java.util.UUID

interface PermissionGroupRepository {
    suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun getPermissionGroup(permissionGroupId: Int): app.bushive.theBouncer.core.result.Result<PermissionGroup, RootError>

    suspend fun getPermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>, RootError>

    suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError>

    suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError>

    suspend fun getPermissionGroupsByUserId(userId: UUID): Result<List<PermissionGroup>, RootError>

    suspend fun addPermissionToGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<PermissionGroupPermission, RootError>

    suspend fun removePermissionFromGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Unit, RootError>
}
