package com.trackmybus.theBouncer.features.v1.data.local.mapper

import com.trackmybus.theBouncer.features.v1.data.local.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.local.model.Permission

object PermissionEntityMapper {
    fun PermissionEntity.toModel() =
        Permission(
            id = this.id.value,
            name = this.name,
            description = this.description,
            permission = this.permission,
            createdAt = this.createdAt,
        )

    fun List<PermissionEntity>.toModel() = map { it.toModel() }
}
