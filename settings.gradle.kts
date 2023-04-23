pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }

    val kotlinVersion: String by settings
    val agpVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.multiplatform") version kotlinVersion
        `maven-publish`
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("com.android.library") version agpVersion
    }
}

rootProject.name = "libpebblecommon"