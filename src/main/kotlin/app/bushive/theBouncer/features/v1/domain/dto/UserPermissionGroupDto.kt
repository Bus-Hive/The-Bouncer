package app.bushive.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserPermissionGroupDto(
    val userId: String?,
    val permissionGroupId: Int? = null,
)
