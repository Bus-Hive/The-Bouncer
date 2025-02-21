package com.trackmybus.theBouncer.features.v1.domain.usecase.permission

import com.trackmybus.theBouncer.core.mapper.ResultMapper.mapResult
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionDto
import com.trackmybus.theBouncer.features.v1.domain.mapper.DTOMapper.toDto
import com.trackmybus.theBouncer.features.v1.domain.repository.permission.PermissionRepository
import io.ktor.util.logging.Logger
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.toLocalDateTime
import java.util.UUID

class PermissionUseCaseImpl(
    private val logger: Logger,
    private val permissionRepository: PermissionRepository,
) : PermissionUseCase {
    override suspend fun addPermission(
        name: String,
        description: String,
        permission: String,
    ): Result<Int> {
        logger.info("Adding permission with name: $name")
        return permissionRepository.add(
            Permission(
                name = name,
                description = description,
                permission = permission,
                createdAt = Clock.System.now().toLocalDateTime(UTC),
            ),
        )
    }

    override suspend fun removePermission(permissionId: Int): Result<Unit> {
        logger.info("Removing permission with id: $permissionId")
        return permissionRepository.deleteById(permissionId)
    }

    override suspend fun updatePermission(
        permissionId: Int,
        name: String,
        description: String,
        permission: String,
    ): Result<Unit> {
        logger.info("Updating permission with id: $permissionId")
        return permissionRepository.update(
            Permission(
                id = permissionId,
                name = name,
                description = description,
                permission = permission,
                createdAt = Clock.System.now().toLocalDateTime(UTC),
            ),
        )
    }

    override suspend fun getPermission(permissionId: Int): Result<PermissionDto?> {
        logger.info("Getting permission with id: $permissionId")
        return permissionRepository.getById(permissionId).mapResult { it?.toDto() }
    }

    override suspend fun getPermissions(): Result<List<PermissionDto>> {
        logger.info("Getting all permissions")
        return permissionRepository.getAll().mapResult { it.map { permission -> permission.toDto() } }
    }

    override suspend fun getPermissionsForUser(userId: String): Result<List<PermissionDto>> {
        logger.info("Getting permissions for user with id: $userId")
        return permissionRepository
            .getPermissionsByUserId(UUID.fromString(userId))
            .mapResult { it.map { permission -> permission.toDto() } }
    }
}
