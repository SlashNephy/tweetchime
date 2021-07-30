plugins {
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.serialization") version "1.5.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

object Versions {
    const val Penicillin = "6.2.1"
    const val Ktor = "1.6.2"

    const val Exposed = "0.32.1"
    const val SQLiteJDBC = "3.36.0.1"

    const val KotlinLogging = "2.0.10"
    const val Logback = "1.2.3"
}

object Libraries {
    const val Penicillin = "blue.starry:penicillin:${Versions.Penicillin}"
    const val KtorClientCIO = "io.ktor:ktor-client-cio:${Versions.Ktor}"
    const val KtorClientSerialization = "io.ktor:ktor-client-serialization:${Versions.Ktor}"

    const val ExposedCore = "org.jetbrains.exposed:exposed-core:${Versions.Exposed}"
    const val ExposedJDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.Exposed}"
    const val SqliteJDBC = "org.xerial:sqlite-jdbc:${Versions.SQLiteJDBC}"

    const val KotlinLogging = "io.github.microutils:kotlin-logging:${Versions.KotlinLogging}"
    const val LogbackCore = "ch.qos.logback:logback-core:${Versions.Logback}"
    const val LogbackClassic = "ch.qos.logback:logback-classic:${Versions.Logback}"

    val ExperimentalAnnotations = setOf(
        "kotlin.time.ExperimentalTime"
    )
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libraries.Penicillin)
    implementation(Libraries.KtorClientCIO)
    implementation(Libraries.KtorClientSerialization)

    implementation(Libraries.ExposedCore)
    implementation(Libraries.ExposedJDBC)
    implementation(Libraries.SqliteJDBC)

    implementation(Libraries.KotlinLogging)
    implementation(Libraries.LogbackCore)
    implementation(Libraries.LogbackClassic)
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
                apiVersion = "1.5"
                languageVersion = "1.5"
                allWarningsAsErrors = true
                verbose = true
            }
        }
    }

    sourceSets.all {
        languageSettings.progressiveMode = true

        Libraries.ExperimentalAnnotations.forEach {
            languageSettings.useExperimentalAnnotation(it)
        }
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes("Main-Class" to "blue.starry.tweetchime.MainKt")
    }
}
