import org.jetbrains.kotlin.konan.target.Family

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.android.library)
//    id("kotlin-parcelize")
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.jetbrains.compose)
    kotlin("multiplatform")
//    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose")
    id("app.cash.sqldelight")
}

val ktorVersion = extra["ktor.version"]

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

//    macosX64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }
//    macosArm64 {
//        binaries {
//            executable {
//                entryPoint = "main"
//            }
//        }
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).filter { it.konanTarget.family == Family.IOS }
        .forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "shared"
                isStatic = true
                with(libs) {
                    export(bundles.decompose)
                    export(essenty.lifecycle)
                }
            }
        }

    jvm("desktop")
    js(IR) {
        browser()
    }

    applyDefaultHierarchyTemplate()

    /*   cocoapods {
           summary = "Some description for the Shared Module"
           homepage = "Link to the Shared Module homepage"
           version = "1.0"
           ios.deploymentTarget = "14.1"
           podfile = project.file("../iosApp/Podfile")
       }*/

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            with(compose) {
                implementation(ui)
                implementation(foundation)
                implementation(material)
                implementation(runtime)
                implementation(components.resources)
            }


            with(libs) {
                implementation(kotlinx.serialization.json)
                implementation(bundles.ktor)
                api(bundles.decompose)
                implementation(image.loader)
                implementation(essenty.lifecycle)
            }


        }

        androidMain {
            dependencies {
                implementation(libs.androidx.media3.exoplayer)
                implementation("androidx.media3:media3-exoplayer:1.3.0")
                implementation("app.cash.sqldelight:android-driver:2.0.2")
            }
        }

        iosMain {
            dependencies {
                implementation("app.cash.sqldelight:native-driver:2.0.2")
            }
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(libs.vlcj)
            implementation("uk.co.caprica:vlcj:4.8.2")
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            with(libs) {
                implementation(ktor.client.js)
                implementation(ktor.client.json.js)
            }
        }
    }
}

android {
    namespace = "com.example.musicapp_kmp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

sqldelight {
    databases {
        create("MusicDatabase") {
            packageName.set("musicapp.cache")
        }
    }
}
