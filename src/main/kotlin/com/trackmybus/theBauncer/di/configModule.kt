package com.trackmybus.theBauncer.di

import com.trackmybus.theBauncer.config.AppConfig
import com.trackmybus.theBauncer.database.postgres.DatabaseFactory
import com.trackmybus.theBauncer.database.postgres.DatabaseFactoryImpl
import org.koin.dsl.module

val configModule =
    module {
        single { AppConfig() }
        single<DatabaseFactory> {
            DatabaseFactoryImpl(
                getLogger<DatabaseFactoryImpl>(),
                get(),
                get()
            )
        }
        single { get<DatabaseFactory>().database }
    }
