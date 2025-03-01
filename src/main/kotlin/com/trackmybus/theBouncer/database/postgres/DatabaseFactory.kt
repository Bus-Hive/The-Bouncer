package com.trackmybus.theBouncer.database.postgres

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import org.jetbrains.exposed.sql.Database

interface DatabaseFactory {
    fun connect()

    fun close()

    suspend fun <T> dbQuery(block: suspend () -> T?): Result<T, DataError.Local>

    var database: Database
}
