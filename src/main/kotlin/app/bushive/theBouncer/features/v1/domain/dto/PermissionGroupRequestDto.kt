package app.bushive.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class PermissionGroupRequestDto(
    val name: String,
    val description: String,
    val isBaseUserGroup: Boolean,
)
