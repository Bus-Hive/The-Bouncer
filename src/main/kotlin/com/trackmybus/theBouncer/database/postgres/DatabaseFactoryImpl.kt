package com.trackmybus.theBouncer.database.postgres

import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.core.result.Result
import com.trackmybus.theBouncer.core.result.ResultHandler.toLocalError
import com.trackmybus.theBouncer.core.result.errors.DataError
import com.trackmybus.theBouncer.features.v1.data.ScheduleSchemaInitializer
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.util.logging.Logger
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseFactoryImpl(
    private val logger: Logger,
    appConfig: AppConfig,
    private val scheduleSchemaInitializer: ScheduleSchemaInitializer,
) : DatabaseFactory {
    private val postgresConfig = appConfig.postgresConfig
    override lateinit var database: Database

    override fun connect() {
        runCatching {
            val connectionPool =
                createHikariDataSource(
                    url =
                        if (postgresConfig.user.isNotEmpty() &&
                            postgresConfig.password.isNotEmpty() &&
                            postgresConfig.database.isNotEmpty()
                        ) {
                            databaseUrlBuilder(
                                jdbcUrl = postgresConfig.jdbcUrl,
                                host = postgresConfig.host,
                                port = postgresConfig.port,
                                defaultDatabase = postgresConfig.database,
                                user = postgresConfig.user,
                                password = postgresConfig.password,
                            )
                        } else {
                            postgresConfig.jdbcUrl
                        },
                    driver = postgresConfig.driverClass,
                    maxPoolSize = postgresConfig.maxPoolSize,
                    autoCommit = postgresConfig.autoCommit,
                )
            database = Database.connect(connectionPool)
            transaction(database) {
                scheduleSchemaInitializer.initSchemas()
            }
        }.onSuccess {
            logger.info("Connected to database")
        }.onFailure {
            logger.error("Failed to connect to database", it)
        }
    }

    override fun close() {
        // used only on Unit tests
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
        host: String,
        port: Int,
        defaultDatabase: String,
        user: String,
        password: String,
    ) = "$jdbcUrl$host:$port/$defaultDatabase?user=$user&password=$password"

    override suspend fun <T> dbQuery(block: suspend () -> T?): Result<T, DataError.Local> =
        try {
            newSuspendedTransaction(Dispatchers.IO) {
                val result = block()
                if (result != null) {
                    Result.Success(result)
                } else {
                    Result.Error(DataError.Local.RecordNotFound)
                }
            }
        } catch (e: Exception) {
            logger.error("Error executing database query", e)
            Result.Error(e.toLocalError())
        }
}
