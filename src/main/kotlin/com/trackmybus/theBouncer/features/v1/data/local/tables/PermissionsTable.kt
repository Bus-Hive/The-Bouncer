package com.trackmybus.theBouncer.features.v1.data.local.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object PermissionsTable : IntIdTable("permissions") {
    val name = varchar("name", 255)
    val description = varchar("description", 255)
    val permission = varchar("permission", 255)
    val createdAt = datetime("created_at")
}
