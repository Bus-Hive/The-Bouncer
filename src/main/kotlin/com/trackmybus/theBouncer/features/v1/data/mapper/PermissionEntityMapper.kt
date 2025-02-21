package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Permission
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PermissionEntityMapper : KoinComponent {
    private val dbFactory: DatabaseFactory by inject()

    suspend fun Permission.toEntity(): Int {
        require(id == null) {
            "Cannot create entity with non-null id"
        }

        return dbFactory.dbQuery {
            val permission =
                PermissionEntity.new {
                    this.name = this@toEntity.name
                    this.description = this@toEntity.description
                    this.permission = this@toEntity.permission
                    this.createdAt = this@toEntity.createdAt
                }

            print(permission)

            return@dbQuery permission.id.value
        }
    }

    suspend fun List<Permission>.toEntities() {
        dbFactory.dbQuery {
            forEach {
                require(it.id == null) {
                    "Cannot create entity with non-null id"
                }

                PermissionEntity.new {
                    this.name = it.name
                    this.description = it.description
                    permission = this.permission
                    this.createdAt = it.createdAt
                }
            }
        }
    }

    fun PermissionEntity.toModel() =
        Permission(
            id = this.id.value,
            name = this.name,
            description = this.description,
            permission = this.permission,
            createdAt = this.createdAt,
        )

    fun List<PermissionEntity>.toModel() = map { it.toModel() }

    suspend fun PermissionEntity.updateModel(model: Permission) {
        require(model.id != null) {
            "Cannot update model with null id"
        }
        require(model.id == this.id.value) {
            "Cannot update model with different id"
        }

        dbFactory.dbQuery {
            this.name = model.name
            this.description = model.description
            this.permission = model.permission
        }
    }
}
