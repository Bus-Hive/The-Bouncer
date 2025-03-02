package com.trackmybus.theBouncer.features.v1.data.local.model

import kotlinx.datetime.LocalDateTime

data class PermissionGroup(
    val id: Int = 0,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val isBaseUserGroup: Boolean,
    val permissions: List<Permission> = emptyList(),
)
