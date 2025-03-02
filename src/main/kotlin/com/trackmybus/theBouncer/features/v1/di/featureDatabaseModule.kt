@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.di

import com.trackmybus.theBouncer.features.v1.data.local.ScheduleSchemaInitializer
import org.koin.dsl.module

val featureDatabaseModule =
    module {
        single { ScheduleSchemaInitializer() }
    }
