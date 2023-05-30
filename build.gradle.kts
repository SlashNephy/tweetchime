plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("com.adarshr.test-logger") version "3.2.0"
    id("net.rdrei.android.buildtimetracker") version "0.11.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("blue.starry:penicillin:6.3.0")
    implementation("io.ktor:ktor-client-java:2.0.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")

    implementation("org.jetbrains.exposed:exposed-jdbc:0.38.2")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    implementation("io.github.microutils:kotlin-logging:2.1.23")
    implementation("ch.qos.logback:logback-classic:1.2.11")
}

kotlin {
    target {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
                apiVersion = "1.7"
                languageVersion = "1.7"
            }
        }
    }

    sourceSets.all {
        languageSettings.progressiveMode = true
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes("Main-Class" to "blue.starry.tweetchime.MainKt")
    }
}
