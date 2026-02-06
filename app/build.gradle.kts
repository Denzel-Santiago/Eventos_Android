import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.proyecto.eventos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.proyecto.eventos"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // ✅ CARGAR local.properties MANUALMENTE
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use {
                localProperties.load(it)
            }
        } else {
            error("No existe local.properties en la raíz del proyecto")
        }

        val baseUrl = localProperties.getProperty("BASE_URL")
            ?: error("BASE_URL no está definido en local.properties")

        val authUrl = localProperties.getProperty("AUTH_URL")
            ?: error("AUTH_URL no está definido en local.properties")

        val eventsUrl = localProperties.getProperty("EVENTS_URL")
            ?: error("EVENTS_URL no está definido en local.properties")

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        buildConfigField("String", "AUTH_URL", "\"$authUrl\"")
        buildConfigField("String", "EVENTS_URL", "\"$eventsUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.com.squareup.retrofit2.retrofit)
    implementation(libs.com.squareup.retrofit2.converter.json)
}
