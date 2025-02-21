package com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.features.v1.data.dao.userPermissionGroup.UserPermissionGroupDao
import com.trackmybus.theBouncer.features.v1.data.mapper.UserPermissionGroupEntityMapper.toModel
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import io.ktor.util.logging.Logger
import java.util.UUID

class UserPermissionGroupRepositoryImpl(
    private val logger: Logger,
    private val userPermissionGroupDao: UserPermissionGroupDao,
) : UserPermissionGroupRepository {
    override suspend fun getAll(): Result<List<UserPermissionGroup>> =
        userPermissionGroupDao
            .getAllUserPermissionGroups()
            .mapResult { it.toModel() }
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched all user permissions") }
                result.onFailure { logger.error("Error fetching all user permissions", it) }
            }

    override suspend fun getByUserId(id: UUID): Result<List<UserPermissionGroup>> =
        userPermissionGroupDao
            .getUserPermissionGroupsByUserId(id)
            .mapResult { it.toModel() }
            .also { result ->
                result.onSuccess { logger.info("Successfully fetched user permission with id: $id") }
                result.onFailure { logger.error("Error fetching user permission with id: $id", it) }
            }

    override suspend fun add(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        userPermissionGroupDao
            .addUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully added user permission: ${userPermissionGroup.permissionGroupId}") }
                result.onFailure { logger.error("Error adding user permission: ${userPermissionGroup.permissionGroupId}", it) }
            }

    override suspend fun add(userPermissionGroups: List<UserPermissionGroup>): Result<Unit> =
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

    override suspend fun update(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        userPermissionGroupDao
            .updateUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully updated user permission: ${userPermissionGroup.permissionGroupId}") }
                result.onFailure { logger.error("Error updating user permission: ${userPermissionGroup.permissionGroupId}", it) }
            }

    override suspend fun deleteById(userPermissionGroup: UserPermissionGroup): Result<Unit> =
        userPermissionGroupDao
            .deleteUserPermissionGroup(userPermissionGroup)
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted user permission with id: $userPermissionGroup") }
                result.onFailure { logger.error("Error deleting user permission with id: $userPermissionGroup", it) }
            }

    override suspend fun deleteAll(): Result<Unit> =
        userPermissionGroupDao
            .deleteAllUserPermissionGroups()
            .also { result ->
                result.onSuccess { logger.info("Successfully deleted all user permissions") }
                result.onFailure { logger.error("Error deleting all user permissions", it) }
            }
}
