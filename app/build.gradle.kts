plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

// Firebase is only used for push-notification testing. Drop the app's
// google-services.json into app/ to enable it; the app builds and the chatbot
// works without it.
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}

// Single source for the SDK version: used for the dependency and shown on the
// Info screen.
val ymChatVersion = "3.5.5"

android {
    namespace = "ai.yellow.demoapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "ai.yellow.demoapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        buildConfigField("String", "YMCHAT_VERSION", "\"$ymChatVersion\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation("com.github.yellowmessenger:YMChatbot-Android:v$ymChatVersion")

    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging")

    testImplementation("junit:junit:4.13.2")
    // Real org.json for JVM unit tests (Android's is a stub there).
    testImplementation("org.json:json:20240303")
}
