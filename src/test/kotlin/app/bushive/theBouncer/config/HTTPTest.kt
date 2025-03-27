package app.bushive.theBouncer.config

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.options
import io.ktor.client.request.patch
import io.ktor.client.request.put
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

class HTTPTest {
    @Test
    fun configureHTTP_allowsSpecifiedMethods() =
        testApplication {
            application {
                configureHTTP()
                routing {
                    get("/") { call.respond("") }
                    options("/") { call.respondText("OK") }
                    put("/") { call.respondText("OK") }
                    delete("/") { call.respondText("OK") }
                    patch("/") { call.respondText("OK") }
                }
            }
            client.options("/").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
            client.put("/").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
            client.delete("/").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
            client.patch("/").apply {
                assertEquals(HttpStatusCode.OK, status)
            }
        }

    @Test
    fun configureHTTP_allowsSpecifiedHeaders() =
        testApplication {
            application {
                configureHTTP()
                routing {
                    get("/") { call.respond("") }
                }
            }
            client
                .get("/") {
                    header(HttpHeaders.Authorization, "Bearer token")
                }.apply {
                    kotlin.test.assertEquals(HttpStatusCode.OK, status)
                }
            client
                .get("/") {
                    header("MyCustomHeader", "customValue")
                }.apply {
                    kotlin.test.assertEquals(HttpStatusCode.OK, status)
                }
        }

    @Test
    fun configureHTTP_allowsAnyHost() =
        testApplication {
            application {
                configureHTTP()
                routing {
                    get("/") { call.respond("") }
                }
            }
            client
                .get("/") {
                    header(HttpHeaders.Origin, "http://example.com")
                }.apply {
                    kotlin.test.assertEquals(HttpStatusCode.OK, status)
                }
            client
                .get("/") {
                    header(HttpHeaders.Origin, "http://another-example.com")
                }.apply {
                    kotlin.test.assertEquals(HttpStatusCode.OK, status)
                }
        }
}
