plugins {
    alias(libs.plugins.android.library).apply(false)
    alias(libs.plugins.jetbrains.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.jvm).apply(false)
    alias(libs.plugins.kotlin.parcelize).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
    id("com.android.application").version("7.4.0").apply(false)
    id("com.android.library").version("7.4.0").apply(false)
    id("org.jetbrains.compose").version("1.6.2") apply false
    id("app.cash.sqldelight").version("2.0.2").apply(false)
    kotlin("android").version("1.8.0").apply(false)
    kotlin("multiplatform").version("1.9.22").apply(false)
    kotlin("jvm") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
    }
}
