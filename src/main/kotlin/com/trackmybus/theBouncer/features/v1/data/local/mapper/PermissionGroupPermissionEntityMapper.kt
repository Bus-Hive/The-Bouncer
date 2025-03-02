package com.trackmybus.theBouncer.features.v1.data.local.mapper

import com.trackmybus.theBouncer.features.v1.data.local.entity.PermissionGroupPermissionEntity
import com.trackmybus.theBouncer.features.v1.data.local.model.PermissionGroupPermission

object PermissionGroupPermissionEntityMapper {
    fun PermissionGroupPermissionEntity.toModel() =
        PermissionGroupPermission(
            permissionGroupId = this.permissionGroupId.value,
            permissionId = permissionId.value,
        )

    fun List<PermissionGroupPermissionEntity>.toModel() = map { it.toModel() }
}
