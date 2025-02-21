package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.UserPermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.model.UserPermissionGroup
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
import org.jetbrains.exposed.dao.id.EntityID
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserPermissionGroupEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()

    suspend fun UserPermissionGroup.toEntity() {
        require(userId != null) {
            "Cannot create entity with null userId"
        }

        require(permissionGroupId != null) {
            "Cannot create entity with null permissionId"
        }

        dbFactory.dbQuery {
            UserPermissionGroupEntity.new {
                this.userId = EntityID(this@toEntity.userId, UsersTable)
                this.permissionGroupId = EntityID(this@toEntity.permissionGroupId, PermissionsTable)
            }
        }
    }

    suspend fun List<UserPermissionGroup>.toEntities() {
        dbFactory.dbQuery {
            forEach {
                require(it.userId != null) {
                    "Cannot create entity with null userId"
                }

                require(it.permissionGroupId != null) {
                    "Cannot create entity with null permissionId"
                }

                UserPermissionGroupEntity.new {
                    this.userId = EntityID(it.userId, UsersTable)
                    this.permissionGroupId = EntityID(it.permissionGroupId, PermissionsTable)
                }
            }
        }
    }

    fun UserPermissionGroupEntity.toModel() =
        UserPermissionGroup(
            userId = this.userId.value,
            permissionGroupId = this.permissionGroupId.value,
        )

    fun List<UserPermissionGroupEntity>.toModel() = map { it.toModel() }

    suspend fun UserPermissionGroupEntity.updateModel(model: UserPermissionGroup) {
        require(model.userId != null) {
            "Cannot update model with null userId"
        }

        require(model.permissionGroupId != null) {
            "Cannot update model with null permissionId"
        }

        require(model.userId == this.userId.value) {
            "Cannot update model with different userId"
        }

        require(model.permissionGroupId == this.permissionGroupId.value) {
            "Cannot update model with different permissionId"
        }

        dbFactory.dbQuery {
            this.userId = EntityID(model.userId, UsersTable)
            this.permissionGroupId = EntityID(model.permissionGroupId, PermissionsTable)
        }
    }
}
