package com.trackmybus.theBouncer.database.postgres

import org.jetbrains.exposed.sql.Database

interface DatabaseFactory {
    fun connect()

    fun close()

    suspend fun <T> dbQuery(block: suspend () -> T): T

    var database: Database
}
