package com.trackmybus.theBouncer.features.v1.data

import com.trackmybus.theBouncer.features.v1.data.tables.PermissionGroupPermissionTable
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionGroupsTable
import com.trackmybus.theBouncer.features.v1.data.tables.PermissionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.SessionsTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersPermissionGroupTable
import com.trackmybus.theBouncer.features.v1.data.tables.UsersTable
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
