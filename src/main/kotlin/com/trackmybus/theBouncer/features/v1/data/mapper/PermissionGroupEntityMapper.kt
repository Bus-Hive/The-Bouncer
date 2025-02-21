package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.features.v1.data.entity.PermissionGroupEntity
import kotlin.getValue

object PermissionGroupEntityMapper {
    fun PermissionGroupEntity.toModel() =
        com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup(
            id = id.value,
            name = name,
            description = description,
            isBaseUserGroup = isBaseUserGroup,
            createdAt = createdAt,
        )

    fun List<PermissionGroupEntity>.toModel() = map { it.toModel() }
}
