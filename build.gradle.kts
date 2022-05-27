plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("com.adarshr.test-logger") version "3.2.0"
    id("net.rdrei.android.buildtimetracker") version "0.11.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("blue.starry:penicillin:6.2.3")
    implementation("io.ktor:ktor-client-cio:2.0.2")
    implementation("io.ktor:ktor-client-serialization:1.6.8")

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
                apiVersion = "1.6"
                languageVersion = "1.6"
                allWarningsAsErrors = true
                verbose = true
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
