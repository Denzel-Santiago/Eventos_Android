// build.gradle.kts (Project: Eventos)

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.secrets.gradle) apply false
    
    // Add these to make them available to subprojects
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
