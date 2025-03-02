package com.trackmybus.theBouncer.features.v1.data.local.model

import kotlinx.datetime.LocalDateTime

data class Permission(
    val id: Int? = null,
    val name: String = "",
    var description: String = "",
    val permission: String = "",
    val createdAt: LocalDateTime,
)
