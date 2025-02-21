package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.config.TheBouncer
import com.trackmybus.theBouncer.core.api.respondError
import com.trackmybus.theBouncer.core.api.respondSuccess
import com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword.LoginRequestDto
import com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword.RegisterRequestDto
import com.trackmybus.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

@Resource("/emailAndPassword")
class EmailAndPassword(
    val parent: TheBouncer.V1.Public,
) {
    @Resource("/login")
    class Login(
        val parent: EmailAndPassword,
    )

    @Resource("/register")
    class Register(
        val parent: EmailAndPassword,
    )
}

fun Application.emailAndPasswordRoutes() {
    routing {
        loginRoute()
        registerRoute()
    }
}

fun Route.loginRoute() {
    val emailPasswordUseCase: EmailPasswordUseCase by application.inject()
    post<EmailAndPassword.Login, LoginRequestDto> { resource, body ->
        print(body)
        runCatching {
            emailPasswordUseCase.loginUser(body.email, body.password)
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to login",
                details = it.message,
            )
        }
    }
}

fun Route.registerRoute() {
    val emailPasswordUseCase: EmailPasswordUseCase by application.inject()
    post<EmailAndPassword.Register, RegisterRequestDto> { resource, body ->
        runCatching {
            emailPasswordUseCase.registerUser(
                email = body.email,
                password = body.password,
                firstName = body.firstName,
                lastName = body.lastName,
            )
        }.onSuccess {
            call.respondSuccess(it.getOrNull())
        }.onFailure {
            call.respondError(
                statusCode = HttpStatusCode.InternalServerError,
                errorCode = 500,
                message = "Failed to register",
                details = it.message,
            )
        }
    }
}
