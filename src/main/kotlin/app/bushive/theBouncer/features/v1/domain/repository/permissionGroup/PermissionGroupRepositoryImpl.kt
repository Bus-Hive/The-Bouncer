package app.bushive.theBouncer.features.v1.domain.repository.permissionGroup

import app.bushive.theBouncer.core.mapper.ResultMapper.mapResult
import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroup.PermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission.PermissionGroupPermissionDao
import app.bushive.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroup
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroupPermission
import io.ktor.util.logging.Logger
import java.util.UUID

class PermissionGroupRepositoryImpl(
    private val logger: Logger,
    private val permissionGroupDao: PermissionGroupDao,
    private val permissionGroupPermissionDao: PermissionGroupPermissionDao,
    private val userPermissionGroupDao: UserPermissionGroupDao,
) : PermissionGroupRepository {
    override suspend fun addPermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError> =
        permissionGroupDao.addPermissionGroup(permissionGroup)

    override suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroup, RootError> =
        permissionGroupDao.getPermissionGroup(permissionGroupId)

    override suspend fun getPermissionGroups(): Result<List<PermissionGroup>, RootError> = permissionGroupDao.getPermissionGroups()

    override suspend fun getBasePermissionGroups(): Result<List<PermissionGroup>, RootError> = permissionGroupDao.getBasePermissionGroups()

    override suspend fun updatePermissionGroup(permissionGroup: PermissionGroup): Result<PermissionGroup, RootError> =
        permissionGroupDao.updatePermissionGroup(permissionGroup)

    override suspend fun deletePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError> =
        permissionGroupDao.deletePermissionGroup(permissionGroupId)

    override suspend fun getPermissionGroupsByUserId(userId: UUID): Result<List<PermissionGroup>, RootError> =
        userPermissionGroupDao.getUserPermissionGroupsByUserId(userId).mapResult { userPermissionGroup ->
            userPermissionGroup.mapNotNull { userPermissionGroup ->
                userPermissionGroup.permissionGroupId?.let { permissionGroupId ->
                    permissionGroupDao.getPermissionGroup(permissionGroupId).getDataOrNull()
                }
            }
        }

    override suspend fun addPermissionToGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<PermissionGroupPermission, RootError> =
        permissionGroupPermissionDao.addPermissionGroupPermission(permissionGroupId, permissionId)

    override suspend fun removePermissionFromGroup(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Unit, RootError> = permissionGroupPermissionDao.deletePermissionGroupPermission(permissionGroupId, permissionId)
}
