@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.di

import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val loggerModule =
    module {
        factory { (clazz: Class<*>) -> LoggerFactory.getLogger(clazz) }
    }

inline fun <reified T : Any> Scope.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
