@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.di

import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.config.ServerConfig
import com.trackmybus.theBouncer.database.DatabaseFactoryForServerTest
import com.trackmybus.theBouncer.database.DatabaseFactoryForUnitTest
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
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
