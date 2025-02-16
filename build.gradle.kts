import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val h2Version: String = project.findProperty("h2_version") as String
val koinVersion: String = project.findProperty("koin_version") as String
val kotlinVersion: String = project.findProperty("kotlin_version") as String
val logbackVersion: String = project.findProperty("logback_version") as String
val postgresVersion: String = project.findProperty("postgres_version") as String
val csvSerializationVersion: String = project.findProperty("csv_serialization_version") as String
val exposedVersion: String = project.findProperty("exposed_version") as String
val hikaricpVersion: String = project.findProperty("hikaricp_version") as String
val mockkVersion: String = project.findProperty("mockk_version") as String

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("org.jetbrains.dokka") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("com.github.ben-manes.versions") version "0.52.0"
}

group = "com.trackmybus.theKeg"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.test {
    jvmArgs = listOf("-Xmx6g")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-cors-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-swagger-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    // Database dependencies
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    // Koin dependencies
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    implementation("io.insert-koin:koin-test:$koinVersion")
    // Logging dependencies
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // CSV serialization dependencies
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:$csvSerializationVersion")
    // Exposed dependencies
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    // cache
    implementation("com.ucasoft.ktor:ktor-simple-cache:0.51.2")
    implementation("com.ucasoft.ktor:ktor-simple-redis-cache:0.51.2")
    implementation("com.ucasoft.ktor:ktor-simple-memory-cache:0.51.2")
    testImplementation("com.ucasoft.ktor:ktor-simple-cache:0.51.2")
    // Test dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    ignoreFailures.set(true)
    baseline.set(file("config/ktlint/baseline.xml"))
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    filter {
        exclude("**/style-violations.kt")
        exclude("**/.gradle/**")
        exclude("**/build/**")
        exclude("**/.kotlin/**")
        exclude("**/docs/**")
        exclude("**/idea/**")
        exclude("**/build.gradle.kts") // Exclude build.gradle.kts file
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
