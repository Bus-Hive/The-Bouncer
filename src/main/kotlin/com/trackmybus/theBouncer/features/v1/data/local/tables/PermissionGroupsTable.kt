package com.trackmybus.theBouncer.features.v1.data.local.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PermissionGroupsTable : IntIdTable("permission_groups") {
    val name = varchar("name", 50)
    val description = varchar("description", 255)
    val isBaseUserGroup = bool("is_base_user_group")
    val createdAt = datetime("created_at")
}
