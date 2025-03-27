package app.bushive.theBouncer.features.v1.domain.repository.userPermissionGroup

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.ResultHandler.onFailure
import app.bushive.theBouncer.core.result.ResultHandler.onSuccess
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.dao.userPermissionGroup.UserPermissionGroupDao
import app.bushive.theBouncer.features.v1.data.local.model.UserPermissionGroup
import io.ktor.util.logging.Logger
import java.util.UUID

class UserPermissionGroupRepositoryImpl(
    private val logger: Logger,
    private val userPermissionGroupDao: UserPermissionGroupDao,
) : UserPermissionGroupRepository {
    override suspend fun getAll(): Result<List<UserPermissionGroup>, RootError> =
        userPermissionGroupDao
            .getAllUserPermissionGroups()
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched all user permissions") }
                result.onFailure { logger.error("Error fetching all user permissions") }
            }

    override suspend fun getByUserId(id: UUID): Result<List<UserPermissionGroup>, RootError> =
        userPermissionGroupDao
            .getUserPermissionGroupsByUserId(id)
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched user permission with id: $id") }
                result.onFailure { logger.error("Error fetching user permission with id: $id") }
            }

    override suspend fun add(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError> =
        userPermissionGroupDao
            .addUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully added user permission: ${userPermissionGroup.permissionGroupId}") }
                result.onFailure { logger.error("Error adding user permission: ${userPermissionGroup.permissionGroupId}") }
            }

    override suspend fun add(userPermissionGroups: List<UserPermissionGroup>): Result<Unit, RootError> =
        userPermissionGroupDao
            .addUserPermissionGroups(userPermissionGroups)
            .also { result ->
                result.onSuccess {
                    logger.info(
                        "Successfully added user permissions: ${userPermissionGroups.joinToString { it.permissionGroupId.toString() }}",
                    )
                }
                result.onFailure {
                    logger.error(
                        "Error adding user permissions: ${userPermissionGroups.joinToString { it.permissionGroupId.toString() }}",
                        it,
                    )
                }
            }

    override suspend fun update(userPermissionGroup: UserPermissionGroup): Result<UserPermissionGroup, RootError> =
        userPermissionGroupDao
            .updateUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully updated user permission: ${userPermissionGroup.permissionGroupId}") }
                result.onFailure { logger.error("Error updating user permission: ${userPermissionGroup.permissionGroupId}") }
            }

    override suspend fun deleteById(userPermissionGroup: UserPermissionGroup): Result<Unit, RootError> =
        userPermissionGroupDao
            .deleteUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted user permission with id: $userPermissionGroup") }
                result.onFailure { logger.error("Error deleting user permission with id: $userPermissionGroup") }
            }

    override suspend fun deleteAll(): Result<Unit, RootError> =
        userPermissionGroupDao
            .deleteAllUserPermissionGroups()
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted all user permissions") }
                result.onFailure { logger.error("Error deleting all user permissions") }
            }
}
