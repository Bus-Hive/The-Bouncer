package com.trackmybus.theBouncer.database

import com.trackmybus.theBouncer.config.AppConfig
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

class DatabaseFactoryForServerTest(
    private val appConfig: AppConfig,
    private val scheduleSchemaInitializer: ScheduleSchemaInitializer,
) : DatabaseFactory {
    private val postgresConfig = appConfig.postgresConfig
    private lateinit var connectionPool: HikariDataSource
    override lateinit var database: Database

    override fun connect() {
        runCatching {
            connectionPool =
                createHikariDataSource(
                    url =
                        databaseUrlBuilder(
                            jdbcUrl = postgresConfig.jdbcUrl,
                            defaultDatabase = postgresConfig.database,
                            user = postgresConfig.user,
                            password = postgresConfig.password,
                        ),
                    driver = postgresConfig.driverClass,
                    maxPoolSize = postgresConfig.maxPoolSize,
                    autoCommit = postgresConfig.autoCommit,
                )
            database = Database.connect(connectionPool)
            transaction(database) {
                scheduleSchemaInitializer.initSchemas()
            }
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

    private fun createHikariDataSource(
        url: String,
        driver: String,
        maxPoolSize: Int,
        autoCommit: Boolean,
    ) = HikariDataSource(
        HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = maxPoolSize
            isAutoCommit = autoCommit
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        },
    )

    private fun databaseUrlBuilder(
        jdbcUrl: String,
        defaultDatabase: String,
        user: String,
        password: String,
    ) = "$jdbcUrl/$defaultDatabase?user=$user&password=$password"
}
