plugins {
    `kotlin-dsl`
    `version-catalog`
}

java.targetCompatibility = JavaVersion.VERSION_11

repositories {
    mavenLocal()
    maven("https://repo.kotlin.link")
    mavenCentral()
    gradlePluginPortal()
}

val toolsVersion = npmlibs.versions.tools.get()
val kotlinVersion = npmlibs.versions.kotlin.asProvider().get()
val benchmarksVersion = "0.4.6"

dependencies {
    api("space.kscience:gradle-tools:$toolsVersion")
    //plugins form benchmarks
    api("org.jetbrains.kotlinx:kotlinx-benchmark-plugin:$benchmarksVersion")
    //to be used inside build-script only
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.OptIn")
}
