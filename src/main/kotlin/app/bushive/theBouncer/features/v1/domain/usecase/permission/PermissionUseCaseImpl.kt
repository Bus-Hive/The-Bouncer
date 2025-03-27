package app.bushive.theBouncer.features.v1.domain.usecase.permission

import app.bushive.theBouncer.core.mapper.ResultMapper.mapResult
import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.features.v1.data.local.model.Permission
import app.bushive.theBouncer.features.v1.domain.dto.PermissionDto
import app.bushive.theBouncer.features.v1.domain.mapper.DTOMapper.toDto
import app.bushive.theBouncer.features.v1.domain.repository.permission.PermissionRepository
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
    ): Result<PermissionDto, RootError> {
        logger.info("Adding permission with name: $name")
        return permissionRepository
            .add(
                Permission(
                    name = name,
                    description = description,
                    permission = permission,
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            ).mapResult { it.toDto() }
    }

    override suspend fun removePermission(permissionId: Int): Result<Unit, RootError> {
        logger.info("Removing permission with id: $permissionId")
        return permissionRepository.deleteById(permissionId)
    }

    override suspend fun updatePermission(
        permissionId: Int,
        name: String,
        description: String,
        permission: String,
    ): Result<PermissionDto, RootError> {
        logger.info("Updating permission with id: $permissionId")
        return permissionRepository
            .update(
                Permission(
                    id = permissionId,
                    name = name,
                    description = description,
                    permission = permission,
                    createdAt = Clock.System.now().toLocalDateTime(UTC),
                ),
            ).mapResult { it.toDto() }
    }

    override suspend fun getPermission(permissionId: Int): Result<PermissionDto, RootError> {
        logger.info("Getting permission with id: $permissionId")
        return permissionRepository.getById(permissionId).mapResult { it.toDto() }
    }

    override suspend fun getPermissions(): Result<List<PermissionDto>, RootError> {
        logger.info("Getting all permissions")
        return permissionRepository.getAll().mapResult { it.map { permission -> permission.toDto() } }
    }

    override suspend fun getPermissionsForUser(userId: String): Result<List<PermissionDto>, RootError> {
        logger.info("Getting permissions for user with id: $userId")
        return permissionRepository
            .getPermissionsByUserId(UUID.fromString(userId))
            .mapResult { it.map { permission -> permission.toDto() } }
    }
}
