package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.features.v1.data.entity.PermissionEntity
import com.trackmybus.theBouncer.features.v1.data.model.Permission

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
