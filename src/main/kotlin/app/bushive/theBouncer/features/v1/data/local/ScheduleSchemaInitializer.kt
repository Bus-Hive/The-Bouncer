package app.bushive.theBouncer.features.v1.data.local

import app.bushive.theBouncer.features.v1.data.local.tables.PermissionGroupPermissionTable
import app.bushive.theBouncer.features.v1.data.local.tables.PermissionGroupsTable
import app.bushive.theBouncer.features.v1.data.local.tables.PermissionsTable
import app.bushive.theBouncer.features.v1.data.local.tables.SessionsTable
import app.bushive.theBouncer.features.v1.data.local.tables.UsersPermissionGroupTable
import app.bushive.theBouncer.features.v1.data.local.tables.UsersTable
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
