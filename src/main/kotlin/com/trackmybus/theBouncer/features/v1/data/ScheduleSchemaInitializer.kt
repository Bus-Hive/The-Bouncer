package com.trackmybus.theBouncer.features.v1.data

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table

class ScheduleSchemaInitializer {
    private val tablesToInitialize: List<Table> =
        listOf()

    fun initSchemas() {
        tablesToInitialize.forEach { table ->
            SchemaUtils.create(table)
        }
    }
}
