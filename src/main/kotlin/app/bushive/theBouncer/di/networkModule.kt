@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.di

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.features.v1.data.remote.service.apiClient.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.jetty.jakarta.Jetty
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule =
    module {
        single {
            HttpClient(Jetty) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        },
                    )
                }

                install(Logging) {
                    logger = ApiConfig.loggerType
                    level = if (get<AppConfig>().serverConfig.isProd) LogLevel.NONE else LogLevel.ALL
                }

                install(HttpTimeout) {
                    ApiConfig.run {
                        requestTimeoutMillis = TIMEOUT
                        connectTimeoutMillis = TIMEOUT
                        socketTimeoutMillis = TIMEOUT
                    }
                }
            }
        }
    }
