package com.trackmybus.theBouncer.config

import com.trackmybus.theBouncer.core.api.respondSuccess
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.routing.routing

@Resource("/the-bouncer")
class TheKeg {
    @Resource("/v1")
    class V1(
        val parent: TheKeg,
    ){
        @Resource("/health")
        class Health(
            val parent: V1,
        )
    }
}

fun Application.configureRouting() {
    install(Resources)

    docsRoutes()

    routing {
        get<TheKeg.V1.Health> {
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
