package app.bushive.theBouncer.features.v1.resource

import app.bushive.theBouncer.config.TheBouncer
import app.bushive.theBouncer.core.api.respondError
import app.bushive.theBouncer.core.result.ResultHandler.respondResult
import app.bushive.theBouncer.features.v1.domain.dto.emailAndPassword.LoginRequestDto
import app.bushive.theBouncer.features.v1.domain.dto.emailAndPassword.RegisterRequestDto
import app.bushive.theBouncer.features.v1.domain.usecase.emailPassword.EmailPasswordUseCase
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
        runCatching {
            emailPasswordUseCase.loginUser(body.email, body.password)
        }.onSuccess {
            call.respondResult(it)
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
            call.respondResult(it)
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
