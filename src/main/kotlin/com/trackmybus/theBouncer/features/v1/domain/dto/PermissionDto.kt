package com.trackmybus.theBouncer.features.v1.domain.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class PermissionDto(
    val id: Int? = null,
    val name: String = "",
    var description: String = "",
    var permission: String = "",
    val createdAt: LocalDateTime,
)
