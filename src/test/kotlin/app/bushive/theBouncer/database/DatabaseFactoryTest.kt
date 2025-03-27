package app.bushive.theBouncer.database

import app.bushive.theBouncer.config.AppConfig
import app.bushive.theBouncer.config.setupConfig
import app.bushive.theBouncer.database.postgres.DatabaseFactory
import app.bushive.theBouncer.database.postgres.DatabaseFactoryImpl
import app.bushive.theBouncer.di.configureKoinUnitTest
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.testApplication
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertNotNull

class DatabaseFactoryTest : KoinTest {
    private lateinit var appConfig: AppConfig
    private lateinit var databaseFactory: DatabaseFactory

    @Before
    fun setUp() =
        testApplication {
            configureKoinUnitTest()

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
                setupConfig()
                appConfig = get()
                databaseFactory =
                    DatabaseFactoryImpl(LoggerFactory.getLogger(DatabaseFactoryImpl::class.java), get(), get())
                databaseFactory.connect()
            }
        }

    @After
    fun tearDown() {
        databaseFactory.close()
        stopKoin()
    }

    @Test
    fun connect_initializesDatabase() {
        assertNotNull(databaseFactory.database)
    }

    @Test
    fun close_closesConnectionPool() {
        databaseFactory.close()
    }

    @Test
    fun dbQuery_executesSuspendedTransaction() =
        runBlocking {
            val result = databaseFactory.dbQuery { "test" }
            assert(result.getDataOrNull() == "test")
        }

    @Test
    fun dbQuery_executesBlock() =
        runBlocking {
            val block = mockk<suspend () -> String>(relaxed = true)
            databaseFactory.dbQuery { block() }
            coVerify { block() }
        }
}
