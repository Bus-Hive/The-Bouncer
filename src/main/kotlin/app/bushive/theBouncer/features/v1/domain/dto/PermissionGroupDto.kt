package app.bushive.theBouncer.features.v1.domain.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PermissionGroupDto(
    val id: Int = 0,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val isBaseUserGroup: Boolean,
    val permissions: List<PermissionDto> = emptyList(),
)
