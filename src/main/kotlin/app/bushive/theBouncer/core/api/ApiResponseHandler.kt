package app.bushive.theBouncer.core.api

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respondText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend inline fun <reified T> ApplicationCall.respondSuccess(
    data: T,
    code: HttpStatusCode = HttpStatusCode.OK,
    message: String? = null,
) {
    respondText(
        contentType = ContentType.Application.Json,
        status = code,
    ) {
        Json.encodeToString(
            ApiResponse.Success(data = data, message = message, code = code.value),
        )
    }
}

suspend fun ApplicationCall.respondError(
    statusCode: HttpStatusCode,
    errorCode: Int,
    message: String,
    details: String? = null,
) {
    respondText(
        text = Json.encodeToString(ApiResponse.Error(errorCode, message, details)),
        contentType = ContentType.Application.Json,
        status = statusCode,
    )
}
