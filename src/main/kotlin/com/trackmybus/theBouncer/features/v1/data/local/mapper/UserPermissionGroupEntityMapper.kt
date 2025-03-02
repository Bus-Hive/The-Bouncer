package com.trackmybus.theBouncer.features.v1.data.local.mapper

import com.trackmybus.theBouncer.features.v1.data.local.entity.UserPermissionGroupEntity
import com.trackmybus.theBouncer.features.v1.data.local.model.UserPermissionGroup

object UserPermissionGroupEntityMapper {
    fun UserPermissionGroupEntity.toModel() =
        UserPermissionGroup(
            userId = this.userId.value,
            permissionGroupId = this.permissionGroupId.value,
        )

    fun List<UserPermissionGroupEntity>.toModel() = map { it.toModel() }
}
