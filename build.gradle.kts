import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.cludivers"
description = "kz2wdSheepWars"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    implementation("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    implementation("commons-io:commons-io:2.11.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<ShadowJar> {
    dependencies {
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.6.21"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21"))
        include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21"))
        include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.1"))
    }
}
