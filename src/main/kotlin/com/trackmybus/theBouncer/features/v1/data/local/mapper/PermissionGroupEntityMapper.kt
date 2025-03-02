package com.trackmybus.theBouncer.features.v1.data.local.mapper

import com.trackmybus.theBouncer.features.v1.data.local.entity.PermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroup

object PermissionGroupEntityMapper {
    fun PermissionGroupEntity.toModel() =
        PermissionGroup(
            id = id.value,
            name = name,
            description = description,
            isBaseUserGroup = isBaseUserGroup,
            createdAt = createdAt,
        )

    fun List<PermissionGroupEntity>.toModel() = map { it.toModel() }
}
