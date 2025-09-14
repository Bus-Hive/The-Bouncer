package app.bushive.theBouncer.features.v1.data.local.mapper

import app.bushive.theBouncer.features.v1.data.local.entity.UserPermissionGroupEntity
import app.bushive.theBouncer.features.v1.data.local.model.UserPermissionGroup

object UserPermissionGroupEntityMapper {
    fun UserPermissionGroupEntity.toModel() =
        UserPermissionGroup(
            userId = this.userId.value,
            permissionGroupId = this.permissionGroupId.value,
        )

    fun List<UserPermissionGroupEntity>.toModel() = map { it.toModel() }
}
