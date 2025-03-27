package app.bushive.theBouncer.features.v1.resource

import app.bushive.theBouncer.config.TheBouncer
import app.bushive.theBouncer.core.api.respondError
import app.bushive.theBouncer.core.result.ResultHandler.isFailure
import app.bushive.theBouncer.core.result.ResultHandler.respondResult
import app.bushive.theBouncer.features.v1.domain.usecase.google.GoogleUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

@Resource("/google")
class GoogleResource(
    val parent: TheBouncer.V1.Public,
) {
    @Resource("/login")
    class Login(
        val parent: GoogleResource,
    )

    @Resource("/callback")
    class Callback(
        val parent: GoogleResource,
        val code: String? = null,
    )
}

fun Application.googleAuthRoutes() {
    routing {
        googleLoginRoute()
        googleCallbackRoute()
    }
}

fun Route.googleLoginRoute() {
    val googleUseCase: GoogleUseCase by application.inject()
    get<GoogleResource.Login> {
        val googleAuthUrl = googleUseCase.getGoogleLoginUrl()
        val url = googleAuthUrl.getDataOrNull()
        if (googleAuthUrl.isFailure() || url == null) {
            call.respondResult(googleAuthUrl)
            return@get
        }

        call.respondRedirect(url)
    }
}

fun Route.googleCallbackRoute() {
    val googleUseCase: GoogleUseCase by application.inject()

    get<GoogleResource.Callback> { resource ->
        val code =
            resource.code ?: run {
                call.respondError(statusCode = HttpStatusCode.BadRequest, errorCode = 400, message = "Missing code")
                return@get
            }

        val tokens = googleUseCase.callback(code)
        call.respondResult(tokens)
    }
}
