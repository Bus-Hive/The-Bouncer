@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionRequestDto
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionsResourceTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory

    @Before
    fun setup() {
        configureKoinUnitTest()
        databaseFactory = get()
        databaseFactory.connect()
    }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    private fun Application.testModule() {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(Resources)
        routing {
            permissionRoutes()
        }
    }

    @Test
    @Ignore
    fun `test add permission success`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/permissions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        Json.encodeToString(
                            PermissionRequestDto.serializer(),
                            PermissionRequestDto(
                                name = "Test Permission",
                                description = "Test Description",
                                permission = "test.permission",
                            ),
                        ),
                    )
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test add permission failure - invalid input`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/permissions") {
                    contentType(ContentType.Application.Json)
                    setBody("") // Invalid JSON
                }

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun `test remove permission success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "Updated Permission",
                            description = "Updated Description",
                            permission = "updated.permission",
                        ),
                    ),
                )
            }
            val response = client.delete("/the-bouncer/v1/protected/permissions?permissionId=1")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test remove permission failure - missing permission ID`() =
        testApplication {
            application { testModule() }

            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "Test Permission",
                            description = "Test Description",
                            permission = "test.permission",
                        ),
                    ),
                )
            }

            val response = client.delete("/the-bouncer/v1/protected/permissions")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    @Ignore
    fun `test update permission success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "Test Permission",
                            description = "Test Description",
                            permission = "test.permission",
                        ),
                    ),
                )
            }
            val requestDto =
                PermissionRequestDto(
                    name = "Updated Permission",
                    description = "Updated Description",
                    permission = "updated.permission",
                )

            val response =
                client.put("/the-bouncer/v1/protected/permissions?permissionId=1") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(PermissionRequestDto.serializer(), requestDto))
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test update permission failure - missing permission ID`() =
        testApplication {
            application { testModule() }

            val response =
                client.put("/the-bouncer/v1/protected/permissions") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        Json.encodeToString(
                            PermissionRequestDto.serializer(),
                            PermissionRequestDto(
                                name = "Updated Permission",
                                description = "Updated Description",
                                permission = "updated.permission",
                            ),
                        ),
                    )
                }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test get permission failure - missing permission ID`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permissions")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test get permissions success`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permissions/all")

            assertEquals(HttpStatusCode.OK, response.status)
        }
}
