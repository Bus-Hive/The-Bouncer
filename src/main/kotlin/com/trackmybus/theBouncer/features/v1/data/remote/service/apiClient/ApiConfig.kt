package com.trackmybus.theBouncer.features.v1.data.remote.service.apiClient

import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger

object ApiConfig {
    const val TIMEOUT = 60000L
    const val PING_INTERVAL = 12000L
    val loggerType = Logger.DEFAULT
}
