import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }
    js(IR) {
        browser { commonWebpackConfig { outputFileName = "composeApp.js" } }
        binaries.executable()
    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
        }
        androidMain.dependencies { implementation(libs.activity.compose) }
        jsMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-dom-api-compat:${libs.versions.kotlin.get()}")
            implementation(npm("sql.js", libs.versions.sqljs.get()))
        }
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}

android {
    namespace = "co.edu.mipuente.kmp"
    compileSdk = 36
    defaultConfig {
        applicationId = "co.edu.mipuente.kmp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

compose.resources { packageOfResClass = "mipuentefinancierokmp.composeapp.generated.resources" }
