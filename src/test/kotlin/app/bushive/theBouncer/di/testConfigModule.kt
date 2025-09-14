@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.di

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.config.ServerConfig
import app.bushive.theBouncer.database.DatabaseFactoryForServerTest
import app.bushive.theBouncer.database.DatabaseFactoryForUnitTest
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import org.koin.dsl.module

val testConfigModulesForUnitTest =
    module {
        single {
            AppConfig().apply {

                serverConfig = ServerConfig(isProd = false)
            }
        }
        single<DatabaseFactory> { DatabaseFactoryForUnitTest(get()) }
        single { get<DatabaseFactory>().database }
    }

val testConfigModulesForIntegrationTest =
    module {
        single { AppConfig() }
        single<DatabaseFactory> { DatabaseFactoryForServerTest(get(), get()) }
        single { get<DatabaseFactory>().database }
    }
