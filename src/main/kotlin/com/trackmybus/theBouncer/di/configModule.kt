@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.di

import com.trackmybus.theBouncer.config.AppConfig
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.database.postgres.DatabaseFactoryImpl
import org.koin.dsl.module

val configModule =
    module {
        single { AppConfig() }
        single<DatabaseFactory> {
            DatabaseFactoryImpl(
                getLogger<DatabaseFactoryImpl>(),
                get(),
                get(),
            )
        }
        single { get<DatabaseFactory>().database }
    }
