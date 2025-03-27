package app.bushive.theBouncer.features.v1.data.local.dao.permissionGroupPermission

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.ResultHandler.addMessage
import app.bushive.theBouncer.core.result.ResultHandler.onFailure
import app.bushive.theBouncer.core.result.RootError
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.features.v1.data.local.entity.PermissionGroupPermissionEntity
import app.bushive.theBouncer.features.v1.data.local.mapper.PermissionGroupPermissionEntityMapper.toModel
import app.bushive.theBouncer.features.v1.data.local.model.PermissionGroupPermission
import app.bushive.theBouncer.features.v1.data.local.tables.PermissionGroupPermissionTable
import app.bushive.theBouncer.features.v1.data.local.tables.PermissionGroupsTable
import app.bushive.theBouncer.features.v1.data.local.tables.PermissionsTable
import io.ktor.util.logging.Logger
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class PermissionGroupPermissionDaoImpl(
    private val logger: Logger,
    private val dbFactory: DatabaseFactory,
) : PermissionGroupPermissionDao {
    override suspend fun addPermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<PermissionGroupPermission, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupPermissionEntity
                    .new {
                        this.permissionGroupId = EntityID(permissionGroupId, PermissionGroupsTable)
                        this.permissionId = EntityID(permissionId, PermissionsTable)
                    }.toModel()
            }.addMessage(
                success = "Permission group permission added successfully",
                failure = "Failed to add permission group permission",
            ).onFailure {
                logger.error("Failed to add permission group permission")
            }

    override suspend fun getPermissionGroupPermissions(permissionGroupId: Int): Result<List<Int>, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupPermissionEntity
                    .find { PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId }
                    .map { it.permissionId.value }
            }.addMessage(
                success = "Permission group permissions retrieved successfully",
                failure = "Failed to get permission group permissions",
            ).onFailure {
                logger.error("Failed to get permission group permissions")
            }

    override suspend fun deletePermissionGroupPermission(
        permissionGroupId: Int,
        permissionId: Int,
    ): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupPermissionEntity
                    .find {
                        (PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId).and {
                            PermissionGroupPermissionTable.permissionId eq
                                permissionId
                        }
                    }.single()
                    .delete()
            }.addMessage(
                success = "Permission group permission deleted successfully",
                failure = "Failed to delete permission group permission",
            ).onFailure {
                logger.error("Failed to delete permission group permission")
            }

    override suspend fun deletePermissionGroupPermissions(permissionGroupId: Int): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupPermissionEntity
                    .find { PermissionGroupPermissionTable.permissionGroupId eq permissionGroupId }
                    .forEach { it.delete() }
            }.addMessage(
                success = "Permission group permissions deleted successfully",
                failure = "Failed to delete permission group permissions",
            ).onFailure {
                logger.error("Failed to delete permission group permissions")
            }

    override suspend fun deletePermissionPermissions(permissionId: Int): Result<Unit, RootError> =
        dbFactory
            .dbQuery {
                PermissionGroupPermissionEntity
                    .find { PermissionGroupPermissionTable.permissionId eq permissionId }
                    .forEach { it.delete() }
            }.addMessage(
                success = "Permission permissions deleted successfully",
                failure = "Failed to delete permission permissions",
            ).onFailure {
                logger.error("Failed to delete permission permissions")
            }
}
