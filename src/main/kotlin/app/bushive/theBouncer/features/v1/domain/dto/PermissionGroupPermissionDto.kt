package app.bushive.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class PermissionGroupPermissionDto(
    val permissionGroupId: Int,
    val permissionId: Int,
)
