package com.trackmybus.theBouncer.di

import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.java

val loggerModule =
    module {
        factory { (clazz: Class<*>) -> LoggerFactory.getLogger(clazz) }
    }

inline fun <reified T : Any> Scope.getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
