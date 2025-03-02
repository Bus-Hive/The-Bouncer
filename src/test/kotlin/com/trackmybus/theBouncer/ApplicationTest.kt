package com.trackmybus.theBouncer

import com.trackmybus.theBouncer.config.configureRouting
import com.trackmybus.theBouncer.database.postgres.DatabaseFactory
import com.trackmybus.theBouncer.di.configureKoinUnitTest
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest : KoinTest {
    private lateinit var databaseFactory: DatabaseFactory

    @Before
    fun setUp() =
        testApplication {
            application {
                (environment.config as MapApplicationConfig).apply {
                    put("ktor.server.isProd", "false")
                    put("postgres.driverClass", "org.h2.Driver")
                    put("postgres.jdbcURL", "jdbc:h2:mem:;DATABASE_TO_UPPER=false;MODE=MYSQL")
                    put("postgres.database", "")
                    put("postgres.host", "localhost")
                    put("postgres.port", "5432")
                    put("postgres.user", "")
                    put("postgres.password", "")
                    put("postgres.maxPoolSize", "1")
                    put("postgres.autoCommit", "true")

                    put("redis.host", "localhost")
                    put("redis.port", "6379")
                    put("redis.password", "password")
                    put("redis.database", "0")

                    put("password.saltLength", "16")
                    put("password.iterations", "10000")
                    put("password.hashLength", "64")
                    put("password.memoryKb", "12345")
                    put("password.parallelism", "1")

                    put("jwt.secret", "secret")
                    put("jwt.issuer", "issuer")
                    put("jwt.audience", "audience")
                    put("jwt.realm", "3600")
                    put("jwt.accessTokenValiditySeconds", "3600")
                    put("jwt.refreshTokenValiditySeconds", "3600")

                    put("google.clientId", "clientId")
                    put("google.clientSecret", "clientSecret")
                    put("google.redirectUri", "redirectUri")
                    put("google.grantType", "grantType")
                }
                module()
            }
            configureKoinUnitTest()

            databaseFactory = get<DatabaseFactory>()
            databaseFactory.connect()
        }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun databaseConnectionEstablished() =
        testApplication {
            assertTrue(databaseFactory.database.url.isNotEmpty())
        }

    @Test
    fun routingConfiguredCorrectly() =
        testApplication {
            application {
                configureRouting()
            }
            val response = client.get("/the-bouncer/v1/health")
            assertEquals(HttpStatusCode.OK, response.status)
        }
}
