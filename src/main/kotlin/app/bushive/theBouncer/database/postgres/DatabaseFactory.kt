package app.bushive.theBouncer.database.postgres

import app.bushive.theBouncer.core.result.Result
import app.bushive.theBouncer.core.result.errors.DataError
import org.jetbrains.exposed.sql.Database

interface DatabaseFactory {
    fun connect()

    fun close()

    suspend fun <T> dbQuery(block: suspend () -> T?): Result<T, DataError.Local>

    var database: Database
}
