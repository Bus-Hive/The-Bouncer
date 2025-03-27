package app.bushive.theBouncer.config

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject

data class ServerConfig(
    val isProd: Boolean,
)

data class PostgresConfig(
    val driverClass: String,
    val jdbcUrl: String,
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int,
    val autoCommit: Boolean,
)

data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String,
    val database: Int,
)

data class PasswordHashConfig(
    val saltLength: Int,
    val hashLength: Int,
    val iterations: Int,
    val memoryKb: Int,
    val parallelism: Int,
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String,
    val accessTokenValiditySeconds: Int,
    val refreshTokenValiditySeconds: Int,
)

data class GoogleConfig(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val grantType: String,
)

class AppConfig {
    lateinit var serverConfig: ServerConfig
    lateinit var postgresConfig: PostgresConfig
    lateinit var redisConfig: RedisConfig
    lateinit var passwordHashConfig: PasswordHashConfig
    lateinit var jwtConfig: JwtConfig
    lateinit var googleConfig: GoogleConfig
}

fun Application.setupConfig() {
    val appConfig by inject<AppConfig>()

    // Server
    val serverObject = environment.config.config("ktor.server")
    val isProd = serverObject.property("isProd").getString().toBoolean()
    appConfig.serverConfig = ServerConfig(isProd)

    // Postgres
    val driverClass = environment.config.property("postgres.driverClass").getString()
    val host = environment.config.property("postgres.host").getString()
    val port =
        environment.config
            .property("postgres.port")
            .getString()
            .toInt()
    val database = environment.config.property("postgres.database").getString()
    val jdbcUrl = environment.config.property("postgres.jdbcURL").getString()
    val user = environment.config.property("postgres.user").getString()
    val password = environment.config.property("postgres.password").getString()
    val maxPoolSize =
        environment.config
            .property("postgres.maxPoolSize")
            .getString()
            .toInt()
    val autoCommit =
        environment.config
            .property("postgres.autoCommit")
            .getString()
            .toBoolean()

    appConfig.postgresConfig =
        PostgresConfig(
            driverClass = driverClass,
            jdbcUrl = jdbcUrl,
            host = host,
            port = port,
            database = database,
            user = user,
            password = password,
            maxPoolSize = maxPoolSize,
            autoCommit = autoCommit,
        )

    // Redis
    val redisHost = environment.config.property("redis.host").getString()
    val redisPort =
        environment.config
            .property("redis.port")
            .getString()
            .toInt()
    val redisPassword = environment.config.property("redis.password").getString()
    val redisDatabase =
        environment.config
            .property("redis.database")
            .getString()
            .toInt()

    appConfig.redisConfig =
        RedisConfig(
            host = redisHost,
            port = redisPort,
            password = redisPassword,
            database = redisDatabase,
        )

    // Password Hash
    val saltLength =
        environment.config
            .property("password.saltLength")
            .getString()
            .toInt()
    val hashLength =
        environment.config
            .property("password.hashLength")
            .getString()
            .toInt()

    val iterations =
        environment.config
            .property("password.iterations")
            .getString()
            .toInt()

    val memoryKb =
        environment.config
            .property("password.memoryKb")
            .getString()
            .toInt()

    val parallelism =
        environment.config
            .property("password.parallelism")
            .getString()
            .toInt()

    appConfig.passwordHashConfig =
        PasswordHashConfig(
            saltLength = saltLength,
            hashLength = hashLength,
            iterations = iterations,
            memoryKb = memoryKb,
            parallelism = parallelism,
        )

    // JWT
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtAccessTokenValiditySeconds =
        environment.config
            .property("jwt.accessTokenValiditySeconds")
            .getString()
            .toInt()
    val jwtRefreshTokenValiditySeconds =
        environment.config
            .property("jwt.refreshTokenValiditySeconds")
            .getString()
            .toInt()

    appConfig.jwtConfig =
        JwtConfig(
            secret = jwtSecret,
            issuer = jwtIssuer,
            audience = jwtAudience,
            realm = jwtRealm,
            accessTokenValiditySeconds = jwtAccessTokenValiditySeconds,
            refreshTokenValiditySeconds = jwtRefreshTokenValiditySeconds,
        )

    // Google
    val googleClientId = environment.config.property("google.clientId").getString()
    val googleClientSecret = environment.config.property("google.clientSecret").getString()
    val googleRedirectUri = environment.config.property("google.redirectUri").getString()
    val googleGrantType = environment.config.property("google.grantType").getString()
    appConfig.googleConfig =
        GoogleConfig(
            clientId = googleClientId,
            clientSecret = googleClientSecret,
            redirectUri = googleRedirectUri,
            grantType = googleGrantType,
        )
}
