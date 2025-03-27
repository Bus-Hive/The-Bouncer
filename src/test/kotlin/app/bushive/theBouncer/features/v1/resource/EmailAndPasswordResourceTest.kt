@file:Suppress("ktlint:standard:filename")

package app.bushive.theBouncer.features.v1.resource

import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.di.configureKoinUnitTest
import app.bushive.theBouncer.features.v1.domain.dto.emailAndPassword.LoginRequestDto
import app.bushive.theBouncer.features.v1.domain.dto.emailAndPassword.RegisterRequestDto
import io.ktor.client.request.post
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
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class EmailAndPasswordResourceTest : KoinTest {
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
            loginRoute()
            registerRoute()
        }
    }

    @Test
    fun `test login success`() =
        testApplication {
            application { testModule() }

            val registerRequestDto =
                RegisterRequestDto(
                    email = "test@example.com",
                    password = "Password123!",
                    firstName = "John",
                    lastName = "Doe",
                )

            val registerResponse =
                client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(RegisterRequestDto.serializer(), registerRequestDto))
                }

            assertEquals(HttpStatusCode.OK, registerResponse.status)
            val requestDto = LoginRequestDto(email = "test@example.com", password = "Password123!")

            val response =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody(Json.encodeToString(LoginRequestDto.serializer(), requestDto))
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test login failure`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/public/emailAndPassword/login") {
                    contentType(ContentType.Application.Json)
                    setBody("")
                }

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }

    @Test
    fun `test register success`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        Json.encodeToString(
                            RegisterRequestDto.serializer(),
                            RegisterRequestDto(
                                email = "test@example.com",
                                password = "Password123!",
                                firstName = "John",
                                lastName = "Doe",
                            ),
                        ),
                    )
                }

            assertEquals(HttpStatusCode.OK, response.status)
        }

    @Test
    fun `test register failure`() =
        testApplication {
            application { testModule() }

            val response =
                client.post("/the-bouncer/v1/public/emailAndPassword/register") {
                    contentType(ContentType.Application.Json)
                    setBody("")
                }

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
}
