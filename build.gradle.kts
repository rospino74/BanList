import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71"
}

buildscript {
    repositories {
        jcenter()
        mavenCentral()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
        classpath("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    }
}

group = "i.marko.banlist"
version = "1.0-STABLE"

apply(plugin = "com.github.johnrengelman.shadow")
apply(plugin = "java")

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib-jdk8"))
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}