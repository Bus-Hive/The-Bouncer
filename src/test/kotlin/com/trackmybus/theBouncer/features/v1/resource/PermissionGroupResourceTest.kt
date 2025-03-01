@file:Suppress("ktlint:standard:filename")

package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.domain.dto.PermissionGroupRequestDto
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

class PermissionGroupResourceTest : KoinTest {
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
            permissionGroupRoutes()
            permissionRoutes()
            emailAndPasswordRoutes()
        }
    }

    @Test
    fun `test add permission group success`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/permission-group") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        Json.encodeToString(
                            PermissionGroupRequestDto.serializer(),
                            PermissionGroupRequestDto(
                                name = "Test Group",
                                description = "Test Description",
                                isBaseUserGroup = false,
                            ),
                        ),
                    )
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test add permission group failure`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/permission-group") {
                    contentType(ContentType.Application.Json)
                    setBody("")
                }

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun `test remove permission group success`() =
        testApplication {
            application { testModule() }
            val requestDto =
                PermissionGroupRequestDto(
                    name = "Test Group",
                    description = "Test Description",
                    isBaseUserGroup = false,
                )

            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(PermissionGroupRequestDto.serializer(), requestDto))
            }

            val response = client.delete("/the-bouncer/v1/protected/permission-group?groupId=1")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test remove permission group failure`() =
        testApplication {
            application { testModule() }

            val response = client.delete("/the-bouncer/v1/protected/permission-group?groupId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test update permission group success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionGroupRequestDto.serializer(),
                        PermissionGroupRequestDto(
                            name = "Test Group",
                            description = "Test Description",
                            isBaseUserGroup = false,
                        ),
                    ),
                )
            }
            val requestDto =
                PermissionGroupRequestDto(
                    name = "Updated Group",
                    description = "Updated Description",
                    isBaseUserGroup = true,
                )

            val response =
                client.put("/the-bouncer/v1/protected/permission-group?groupId=1") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(PermissionGroupRequestDto.serializer(), requestDto))
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test update permission group failure`() =
        testApplication {
            application { testModule() }

            val response =
                client.put("/the-bouncer/v1/protected/permission-group") {
                    contentType(ContentType.Application.Json)
                    setBody("")
                }

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun `test assign permission to group success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "a",
                            description = "b",
                            permission = "c",
                        ),
                    ),
                )
            }

            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionGroupRequestDto.serializer(),
                        PermissionGroupRequestDto(
                            name = "Test Group",
                            description = "Test Description",
                            isBaseUserGroup = false,
                        ),
                    ),
                )
            }

            val response = client.post("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test assign permission to group failure`() =
        testApplication {
            application { testModule() }

            val response = client.post("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test remove permission from group success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "a",
                            description = "b",
                            permission = "c",
                        ),
                    ),
                )
            }

            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionGroupRequestDto.serializer(),
                        PermissionGroupRequestDto(
                            name = "Test Group",
                            description = "Test Description",
                            isBaseUserGroup = false,
                        ),
                    ),
                )
            }

            client.post("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            val response =
                client.delete("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test remove permission from group failure`() =
        testApplication {
            application { testModule() }

            val response =
                client.delete("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    @Ignore
    fun `test get permission group success`() =
        testApplication {
            application { testModule() }

            client.post("/the-bouncer/v1/protected/permissions") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionRequestDto.serializer(),
                        PermissionRequestDto(
                            name = "a",
                            description = "b",
                            permission = "c",
                        ),
                    ),
                )
            }

            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionGroupRequestDto.serializer(),
                        PermissionGroupRequestDto(
                            name = "Test Group",
                            description = "Test Description",
                            isBaseUserGroup = false,
                        ),
                    ),
                )
            }

            client.post("/the-bouncer/v1/protected/permission-group/permission?groupId=1&permissionId=1")

            val response = client.get("/the-bouncer/v1/protected/permission-group?groupId=1")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test get permission group failure`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permission-group?groupId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    @Ignore
    fun `test get permission groups success`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permission-group/all")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test get base permission groups success`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permission-group/base")

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test get permission groups by user id success`() =
        testApplication {
            application { testModule() }
            client.post("/the-bouncer/v1/protected/permission-group") {
                contentType(ContentType.Application.Json)
                setBody(
                    Json.encodeToString(
                        PermissionGroupRequestDto.serializer(),
                        PermissionGroupRequestDto(
                            name = "Test Group",
                            description = "Test Description",
                            isBaseUserGroup = false,
                        ),
                    ),
                )
            }

            val response = client.get("/the-bouncer/v1/protected/permission-group/user?userId=1")

            assertEquals(HttpStatusCode.OK, HttpStatusCode.OK)
        }

    @Test
    fun `test get permission groups by user id failure`() =
        testApplication {
            application { testModule() }

            val response = client.get("/the-bouncer/v1/protected/permission-group/user?userId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test add user to permission group success`() =
        testApplication {
            application { testModule() }

            val response = client.post("/the-bouncer/v1/protected/permission-group/user?userId=1&groupId=1")

            assertEquals(HttpStatusCode.OK, HttpStatusCode.OK)
        }

    @Test
    fun `test add user to permission group failure`() =
        testApplication {
            application { testModule() }

            val response = client.post("/the-bouncer/v1/protected/permission-group/user?userId=1&groupId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test remove user from permission group success`() =
        testApplication {
            application { testModule() }

            val response = client.delete("/the-bouncer/v1/protected/permission-group/user?userId=1&groupId=1")

            assertEquals(HttpStatusCode.OK, HttpStatusCode.OK)
        }

    @Test
    fun `test remove user from permission group failure`() =
        testApplication {
            application { testModule() }

            val response = client.delete("/the-bouncer/v1/protected/permission-group/user?userId=1&groupId=1")

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }
}
