package com.trackmybus.theBouncer.features.v1.data.local

import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionGroupPermissionTable
import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionGroupsTable
import com.trackmybus.theBouncer.features.v1.data.local.tables.PermissionsTable
import com.trackmybus.theBouncer.features.v1.data.local.tables.SessionsTable
import com.trackmybus.theBouncer.features.v1.data.local.tables.UsersPermissionGroupTable
import com.trackmybus.theBouncer.features.v1.data.local.tables.UsersTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

class ScheduleSchemaInitializer {
    private val tablesToInitialize: List<Table> =
        listOf(
            UsersTable,
            SessionsTable,
            PermissionsTable,
            PermissionGroupsTable,
            PermissionGroupPermissionTable,
            UsersPermissionGroupTable,
        )

    fun initSchemas() {
        tablesToInitialize.forEach { table ->
            SchemaUtils.create(table)
        }
    }
}
