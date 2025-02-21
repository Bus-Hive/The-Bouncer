package com.trackmybus.theBouncer.features.v1.domain.mapper

import com.trackmybus.theBouncer.features.v1.data.model.Permission
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroup
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionDto
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupDto

object DTOMapper {
    fun Permission.toDto() =
        PermissionDto(
            id = this@toDto.id,
            name = this@toDto.name,
            description = this@toDto.description,
            permission = this@toDto.permission,
            createdAt = this@toDto.createdAt,
        )

    fun PermissionDto.toModel() =
        Permission(
            id = id,
            name = name,
            description = description,
            permission = permission,
            createdAt = createdAt,
        )

    fun PermissionGroup.toDto() =
        PermissionGroupDto(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt,
            isBaseUserGroup = isBaseUserGroup,
        )
}
