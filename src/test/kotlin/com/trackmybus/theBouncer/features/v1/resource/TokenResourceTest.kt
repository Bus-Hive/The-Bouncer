package com.trackmybus.theBouncer.features.v1.resource

import com.trackmybus.theBouncer.core.api.ApiResponse
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import com.trackmybus.theBouncer.features.v1.domain.dto.AccessTokenDto
import com.trackmybus.theBouncer.features.v1.domain.dto.RefreshTokenDto
import com.trackmybus.theBouncer.features.v1.domain.dto.TokensDto
import com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword.LoginRequestDto
import com.trackmybus.theBouncer.features.v1.domain.dto.emailAndPassword.RegisterRequestDto
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenRoutesTest : KoinTest {
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
            tokenRoutes()
            loginRoute() // Assuming loginRoute is available in the same module
            registerRoute() // Assuming registerRoute is available in the same module
        }
    }

    @Test
    fun `test refresh token success`() =
        testApplication {
            application { testModule() }

            // Step 1: Register a user
            val registerRequestDto =
                RegisterRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                    firstName = "John",
                    lastName = "Doe",
                )

            client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(RegisterRequestDto.serializer(), registerRequestDto))
            }

            // Step 2: Login to get access and refresh tokens
            val loginRequestDto =
                LoginRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                )

            val loginResponse =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(LoginRequestDto.serializer(), loginRequestDto))
                }

            assertEquals(HttpStatusCode.OK, loginResponse.status)

            // Extract refresh token from login response
            val refreshToken =
                (
                    Json.decodeFromString<ApiResponse.Success<TokensDto>>(
                        loginResponse.bodyAsText(),
                    ) as? ApiResponse.Success
                )?.data?.refreshToken!!

            // Step 3: Refresh token
            val refreshResponse =
                client.post("/the-bouncer/v1/public/token/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto(refreshToken)))
                }

            assertEquals(HttpStatusCode.OK, refreshResponse.status)
        }

    @Test
    fun `test refresh token failure - invalid refresh token`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/public/token/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto("invalid-token")))
                }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test validate access token success`() =
        testApplication {
            application { testModule() }

            // Step 1: Register a user
            val registerRequestDto =
                RegisterRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                    firstName = "John",
                    lastName = "Doe",
                )

            client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(RegisterRequestDto.serializer(), registerRequestDto))
            }

            // Step 2: Login to get access and refresh tokens
            val loginRequestDto =
                LoginRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                )

            val loginResponse =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(LoginRequestDto.serializer(), loginRequestDto))
                }

            assertEquals(HttpStatusCode.OK, loginResponse.status)

            // Extract access token from login response
            val accessToken = Json.decodeFromString<ApiResponse.Success<TokensDto>>(loginResponse.bodyAsText()).data.accessToken

            // Step 3: Validate access token
            val validateResponse =
                client.post("/the-bouncer/v1/protected/token/validate/access") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(AccessTokenDto.serializer(), AccessTokenDto(accessToken)))
                }

            assertEquals(HttpStatusCode.OK, validateResponse.status)
        }

    @Test
    fun `test validate access token failure - invalid access token`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/token/validate/access") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(AccessTokenDto.serializer(), AccessTokenDto("invalid-token")))
                }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }

    @Test
    fun `test validate refresh token success`() =
        testApplication {
            application { testModule() }

            // Step 1: Register a user
            val registerRequestDto =
                RegisterRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                    firstName = "John",
                    lastName = "Doe",
                )

            client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(RegisterRequestDto.serializer(), registerRequestDto))
            }

            // Step 2: Login to get access and refresh tokens
            val loginRequestDto =
                LoginRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                )

            val loginResponse =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(LoginRequestDto.serializer(), loginRequestDto))
                }

            assertEquals(HttpStatusCode.OK, loginResponse.status)

            // Extract refresh token from login response
            val refreshToken =
                (
                    Json.decodeFromString<ApiResponse.Success<TokensDto>>(
                        loginResponse.bodyAsText(),
                    ) as? ApiResponse.Success
                )?.data?.refreshToken!!

            // Step 3: Validate refresh token
            val validateResponse =
                client.post("/the-bouncer/v1/protected/token/validate/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto(refreshToken)))
                }

            assertEquals(HttpStatusCode.OK, validateResponse.status)
        }

    @Test
    fun `test validate refresh token failure - invalid refresh token`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/token/validate/refresh") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto("invalid-token")))
                }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }

    @Test
    fun `test revoke session success`() =
        testApplication {
            application { testModule() }

            // Step 1: Register a user
            val registerRequestDto =
                RegisterRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                    firstName = "John",
                    lastName = "Doe",
                )

            client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(RegisterRequestDto.serializer(), registerRequestDto))
            }

            // Step 2: Login to get access and refresh tokens
            val loginRequestDto =
                LoginRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                )

            val loginResponse =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(LoginRequestDto.serializer(), loginRequestDto))
                }

            assertEquals(HttpStatusCode.OK, loginResponse.status)

            // Extract refresh token from login response
            val refreshToken =
                (
                    Json.decodeFromString<ApiResponse.Success<TokensDto>>(
                        loginResponse.bodyAsText(),
                    ) as? ApiResponse.Success
                )?.data?.refreshToken!!
            // Step 3: Revoke session
            val revokeResponse =
                client.post("/the-bouncer/v1/protected/token/revoke") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto(refreshToken)))
                }

            assertEquals(HttpStatusCode.OK, revokeResponse.status)
        }

    @Test
    fun `test revoke session failure - invalid refresh token`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/protected/token/revoke") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RefreshTokenDto.serializer(), RefreshTokenDto("invalid-token")))
                }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }
}
