package com.trackmybus.theBouncer.di

import com.trackmybus.theBouncer.config.setupConfig
import com.trackmybus.theBouncer.features.v1.di.featureModules
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import org.koin.core.context.startKoin
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun configureKoinUnitTest() {
    startKoin {
        slf4jLogger()
        modules(
            testConfigModulesForUnitTest,
            loggerModule,
            *featureModules.toTypedArray(),
        )
    }
    testApplication {
        application {
            (environment.config as MapApplicationConfig).apply {
                put("ktor.server.isProd", "false")
                put("postgres.driverClass", "org.h2.Driver")
                put("postgres.jdbcURL", "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL")
                put("postgres.database", "")
                put("postgres.host", "localhost")
                put("postgres.port", "5432")
                put("postgres.user", "")
                put("postgres.password", "")
                put("postgres.maxPoolSize", "1")
                put("postgres.autoCommit", "true")

                put("redis.host", "localhost")
                put("redis.port", "6379")
                put("redis.password", "password")
                put("redis.database", "0")

                put("password.saltLength", "16")
                put("password.iterations", "1")
                put("password.hashLength", "64")
                put("password.memoryKb", "1024")
                put("password.parallelism", "1")

                put("jwt.secret", "secret")
                put("jwt.issuer", "issuer")
                put("jwt.audience", "audience")
                put("jwt.realm", "3600")
                put("jwt.accessTokenValiditySeconds", "3600")
                put("jwt.refreshTokenValiditySeconds", "3600")

                put("google.clientId", "clientId")
                put("google.clientSecret", "clientSecret")
                put("google.redirectUri", "redirectUri")
                put("google.grantType", "grantType")
            }
            setupConfig()
        }
    }
}

fun Application.configureKoinServerTest() {
    install(Koin) {
        slf4jLogger()
        modules(
            testConfigModulesForIntegrationTest,
            loggerModule,
            *featureModules.toTypedArray(),
        )
    }
}
