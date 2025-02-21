package com.trackmybus.theBouncer.core

import com.trackmybus.theBouncer.core.api.respondError
import com.trackmybus.theBouncer.core.api.respondSuccess
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiResponseHandlerTest {
    @Test
    fun respondSuccess_returnsSerializedSuccessResponse() =
        testApplication {
            application {
                routing {
                    get("/success") {
                        call.respondSuccess("Test Data")
                    }
                }
            }
            client.get("/success").apply {
                assertEquals(HttpStatusCode.OK, status)
                assertEquals("""{"data":"Test Data"}""", bodyAsText())
            }
        }

    @Test
    fun respondError_returnsSerializedErrorResponse() =
        testApplication {
            application {
                routing {
                    get("/error") {
                        call.respondError(HttpStatusCode.NotFound, 404, "Not Found")
                    }
                }
            }
            client.get("/error").apply {
                assertEquals(HttpStatusCode.NotFound, status)
                assertEquals("""{"errorCode":404,"message":"Not Found"}""", bodyAsText())
            }
        }

    @Test
    fun respondError_withDetails_returnsSerializedErrorResponseWithDetails() =
        testApplication {
            application {
                routing {
                    get("/errorWithDetails") {
                        call.respondError(
                            HttpStatusCode.InternalServerError,
                            500,
                            "Internal Server Error",
                            "Stack trace details",
                        )
                    }
                }
            }
            client.get("/errorWithDetails").apply {
                assertEquals(HttpStatusCode.InternalServerError, status)
                assertEquals(
                    """{"errorCode":500,"message":"Internal Server Error","details":"Stack trace details"}""",
                    bodyAsText(),
                )
            }
        }

    @Test
    fun respondError_withNullDetails_returnsSerializedErrorResponseWithoutDetails() =
        testApplication {
            application {
                routing {
                    get("/errorWithNullDetails") {
                        call.respondError(HttpStatusCode.BadRequest, 400, "Bad Request", null)
                    }
                }
            }
            client.get("/errorWithNullDetails").apply {
                assertEquals(HttpStatusCode.BadRequest, status)
                assertEquals("""{"errorCode":400,"message":"Bad Request"}""", bodyAsText())
            }
        }
}
