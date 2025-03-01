package com.trackmybus.theBouncer.features.v1.data.mapper

import com.trackmybus.theBouncer.features.v1.data.entity.PermissionGroupPermissionEntity
import com.trackmybus.theBouncer.features.v1.data.model.PermissionGroupPermission

object PermissionGroupPermissionEntityMapper {
    fun PermissionGroupPermissionEntity.toModel() =
        PermissionGroupPermission(
            permissionGroupId = this.permissionGroupId.value,
            permissionId = permissionId.value,
        )

    fun List<PermissionGroupPermissionEntity>.toModel() = map { it.toModel() }
}
