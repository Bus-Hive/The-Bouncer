package app.bushive.theBouncer.features.v1.domain.repository.userPermissionGroup

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.UserPermissionGroup
import java.util.UUID

interface UserPermissionGroupRepository {
    suspend fun getAll(): Result<List<UserPermissionGroup>, RootError>

    suspend fun getByUserId(id: UUID): Result<List<UserPermissionGroup>, RootError>

    suspend fun add(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError>

    suspend fun add(userPermissionGroups: List<UserPermissionGroup>): Result<Unit, RootError>

    suspend fun update(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError>

    suspend fun deleteById(userPermissionGroup: UserPermissionGroup): Result<Unit, RootError>

    suspend fun deleteAll(): Result<Unit, RootError>
}
