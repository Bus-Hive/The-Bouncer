package com.trackmybus.theBouncer.config

import com.trackmybus.theBouncer.core.api.respondSuccess
import com.trackmybus.theBouncer.features.v1.resource.emailAndPasswordRoutes
import com.trackmybus.theBouncer.features.v1.resource.googleAuthRoutes
import com.trackmybus.theBouncer.features.v1.resource.permissionGroupRoutes
import com.trackmybus.theBouncer.features.v1.resource.permissionRoutes
import com.trackmybus.theBouncer.features.v1.resource.tokenRoutes
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.routing.routing

@Resource("/the-bouncer")
class TheBouncer {
    @Resource("/v1")
    class V1(
        val parent: TheBouncer,
    ) {
        @Resource("/public")
        class Public(
            val parent: V1,
        )

        @Resource("/protected")
        class Protected(
            val parent: V1,
        )

        @Resource("/health")
        class Health(
            val parent: V1,
        )
    }
}

fun Application.configureRouting() {
    install(Resources)
    healthRoutes()
    docsRoutes()
    emailAndPasswordRoutes()
    tokenRoutes()
    permissionRoutes()
    permissionGroupRoutes()
    googleAuthRoutes()
}

fun Application.healthRoutes() {
    routing {
        get<TheBouncer.V1.Health> {
            call.respondSuccess(
                "Welcome to The Bouncer API",
            )
        }
    }
}

fun Application.docsRoutes() {
    routing {
        openAPI(path = "the-bouncer/docs")
        swaggerUI(path = "the-bouncer/docs")
    }
}
