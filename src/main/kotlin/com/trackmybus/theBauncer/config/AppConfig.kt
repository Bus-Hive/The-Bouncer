package com.trackmybus.theBauncer.config

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

data class ServerConfig(
    val isProd: Boolean,
)

data class PostgresConfig(
    val driverClass: String,
    val jdbcUrl: String,
    val database: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int,
    val autoCommit: Boolean,
)

data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String,
    val database: Int,
)

class AppConfig {
    lateinit var serverConfig: ServerConfig
    lateinit var postgresConfig: PostgresConfig
    lateinit var redisConfig: RedisConfig
}

fun Application.setupConfig() {
    val appConfig by inject<AppConfig>()

    // Server
    val serverObject = environment.config.config("ktor.server")
    val isProd = serverObject.property("isProd").getString().toBoolean()
    appConfig.serverConfig = ServerConfig(isProd)

    // Postgres
    val driverClass = environment.config.property("postgres.driverClass").getString()
    val database = environment.config.property("postgres.database").getString()
    val jdbcUrl = environment.config.property("postgres.jdbcURL").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()
    val maxPoolSize =
        environment.config
            .property("postgres.maxPoolSize")
            .getString()
            .toInt()
    val autoCommit =
        environment.config
            .property("postgres.autoCommit")
            .getString()
            .toBoolean()

    appConfig.postgresConfig =
        PostgresConfig(
            driverClass = driverClass,
            jdbcUrl = jdbcUrl,
            database = database,
            user = user,
            password = password,
            maxPoolSize = maxPoolSize,
            autoCommit = autoCommit,
        )

    // Redis
    val redisHost = environment.config.property("redis.host").getString()
    val redisPort =
        environment.config
            .property("redis.port")
            .getString()
            .toInt()
    val redisPassword = environment.config.property("redis.password").getString()
    val redisDatabase =
        environment.config
            .property("redis.database")
            .getString()
            .toInt()

    appConfig.redisConfig =
        RedisConfig(
            host = redisHost,
            port = redisPort,
            password = redisPassword,
            database = redisDatabase,
        )
}
