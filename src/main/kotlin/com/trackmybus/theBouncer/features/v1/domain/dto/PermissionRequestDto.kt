package com.trackmybus.theBouncer.features.v1.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class PermissionRequestDto(
    val name: String,
    val description: String,
    val permission: String,
)
