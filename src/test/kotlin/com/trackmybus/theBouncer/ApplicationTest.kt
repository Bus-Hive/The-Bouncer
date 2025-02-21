package com.trackmybus.theBouncer

import com.trackmybus.theBouncer.config.configureDatabases
import com.trackmybus.theBouncer.config.configureRouting
import com.trackmybus.theBouncer.config.setupConfig
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
                    put("postgres.driverClass", "org.postgresql.Driver")
                    put("postgres.jdbcURL", "jdbc:postgresql://localhost:5432/mydb")
                    put("postgres.database", "mydb")
                    put("postgres.user", "user")
                    put("postgres.password", "password")
                    put("postgres.maxPoolSize", "10")
                    put("postgres.autoCommit", "true")
                    put("redis.host", "localhost")
                    put("redis.port", "6379")
                    put("redis.password", "password")
                    put("redis.database", "0")
                }
                setupConfig()
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
            application { configureDatabases() }
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
