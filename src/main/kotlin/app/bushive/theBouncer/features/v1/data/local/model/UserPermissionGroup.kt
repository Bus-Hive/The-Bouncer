package app.bushive.theBouncer.features.v1.data.local.model

import java.util.UUID

data class UserPermissionGroup(
    val userId: UUID?,
    val permissionGroupId: Int? = null,
)
