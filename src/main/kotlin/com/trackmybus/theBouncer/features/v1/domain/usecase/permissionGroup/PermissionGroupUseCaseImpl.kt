package com.trackmybus.theBouncer.features.v1.domain.usecase.permissionGroup

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.RootError
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupDto
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupPermissionDto
import com.trackmybus.theBouncer.features.v1.domain.dto.UserPermissionGroupDto
import com.trackmybus.theBouncer.features.v1.domain.mapper.DTOMapper.toDto
import com.trackmybus.theBouncer.features.v1.domain.repository.permissionGroup.PermissionGroupRepository
import com.trackmybus.theBouncer.features.v1.domain.repository.userPermissionGroup.UserPermissionGroupRepository
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class PermissionGroupUseCaseImpl(
    private val logger: Logger,
    private val permissionGroupRepository: PermissionGroupRepository,
    private val userPermissionRepository: UserPermissionGroupRepository,
) : PermissionGroupUseCase {
    override suspend fun addPermissionGroup(
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto, RootError> {
        logger.info("Adding permission group with name: $name")
        return permissionGroupRepository
            .addPermissionGroup(
                PermissionGroup(
                    name = name,
                    description = description,
                    isBaseUserGroup = isBaseUserGroup,
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            ).mapResult { it.toDto() }
    }

    override suspend fun removePermissionGroup(permissionGroupId: Int): Result<Boolean, RootError> {
        logger.info("Removing permission group with id: $permissionGroupId")
        return permissionGroupRepository.deletePermissionGroup(permissionGroupId)
    }

    override suspend fun updatePermissionGroup(
        permissionGroupId: Int,
        name: String,
        description: String,
        isBaseUserGroup: Boolean,
    ): Result<PermissionGroupDto, RootError> {
        logger.info("Updating permission group with id: $permissionGroupId")
        return permissionGroupRepository
            .updatePermissionGroup(
                PermissionGroup(
                    id = permissionGroupId,
                    name = name,
                    description = description,
                    isBaseUserGroup = isBaseUserGroup,
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            ).mapResult { it.toDto() }
    }

    override suspend fun assignPermissionToGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<PermissionGroupPermissionDto, RootError> {
        logger.info("Assigning permission with id: $permissionId to group with id: $permissionGroupId")
        return permissionGroupRepository.addPermissionToGroup(permissionGroupId, permissionId).mapResult { it.toDto() }
    }

    override suspend fun removePermissionFromGroup(
        permissionId: Int,
        permissionGroupId: Int,
    ): Result<Unit, RootError> {
        logger.info("Removing permission with id: $permissionId from group with id: $permissionGroupId")
        return permissionGroupRepository.removePermissionFromGroup(permissionGroupId, permissionId)
    }

    override suspend fun getPermissionGroup(permissionGroupId: Int): Result<PermissionGroupDto, RootError> {
        logger.info("Getting permission group with id: $permissionGroupId")
        return permissionGroupRepository.getPermissionGroup(permissionGroupId).mapResult { it.toDto() }
    }

    override suspend fun getPermissionGroups(): Result<List<PermissionGroupDto>, RootError> {
        logger.info("Getting all permission groups")
        return permissionGroupRepository
            .getPermissionGroups()
            .mapResult { it.map { permissionGroup -> permissionGroup.toDto() } }
    }

    override suspend fun getBasePermissionGroups(): Result<List<PermissionGroupDto>, RootError> {
        logger.info("Getting all base permission groups")
        return permissionGroupRepository
            .getBasePermissionGroups()
            .mapResult { it.map { permissionGroup -> permissionGroup.toDto() } }
    }

    override suspend fun getPermissionGroupsByUserId(userId: String): Result<List<PermissionGroupDto>, RootError> {
        logger.info("Getting permission groups for user with id: $userId")
        return permissionGroupRepository
            .getPermissionGroupsByUserId(UUID.fromString(userId))
            .mapResult { it.map { permissionGroup -> permissionGroup.toDto() } }
    }

    override suspend fun assignUserToGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<UserPermissionGroupDto, RootError> {
        logger.info("Assigning user with id: $userId to group with id: $permissionGroupId")
        return userPermissionRepository
            .add(
                UserPermissionGroup(
                    userId = UUID.fromString(userId),
                    permissionGroupId = permissionGroupId,
                ),
            ).mapResult { it.toDto() }
    }

    override suspend fun removeUserFromGroup(
        userId: String,
        permissionGroupId: Int,
    ): Result<Unit, RootError> {
        logger.info("Removing user with id: $userId from group with id: $permissionGroupId")
        return userPermissionRepository.deleteById(
            UserPermissionGroup(
                userId = UUID.fromString(userId),
                permissionGroupId = permissionGroupId,
            ),
        )
    }
}
