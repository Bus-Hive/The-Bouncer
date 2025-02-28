package com.trackmybus.theBouncer.database

import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.features.v1.data.ScheduleSchemaInitializer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactoryForUnitTest(
    private val scheduleSchemaInitializer: ScheduleSchemaInitializer,
) : DatabaseFactory {
    internal lateinit var connectionPool: HikariDataSource

    override lateinit var database: Database

    override fun connect() {
        connectionPool = createHikariDataSource()
        database = Database.connect(connectionPool)
        transaction(database) {
            scheduleSchemaInitializer.initSchemas()
        }
    }

    override fun close() {
        connectionPool.close()
    }

    override suspend fun <T> dbQuery(block: suspend () -> T?): Result<T, DataError.Local> =
        try {
            newSuspendedTransaction(Dispatchers.IO) {
                block()?.let { Result.Success(it) } ?: Result.Error(DataError.Local.RecordNotFound)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Local.Unknown)
        }

    private fun createHikariDataSource() =
        HikariDataSource(
            HikariConfig().apply {
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL"
                maximumPoolSize = 2
                isAutoCommit = true
                validate()
            },
        )
}
