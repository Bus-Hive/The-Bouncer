package com.trackmybus.theBouncer.config

import com.ucasoft.ktor.simpleCache.SimpleCache
import com.ucasoft.ktor.simpleRedisCache.redisCache
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.ext.inject
import kotlin.time.Duration.Companion.days

fun Application.configureCache() {
    val appConfig: AppConfig by inject()
    install(SimpleCache) {
        redisCache {
            invalidateAt = 1.days
            host = appConfig.redisConfig.host
            port = appConfig.redisConfig.port
        }
    }
}
