import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

android {
    namespace = "com.scrapeverything.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.scrapeverything.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["ADMOB_APP_ID"] =
            localProperties.getProperty("ADMOB_APP_ID", "")

        val serverUrl = localProperties.getProperty("SERVER_URL", "http://10.0.2.2:8080/")
        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
        buildConfigField(
            "String",
            "ADMOB_BANNER_ID",
            "\"${localProperties.getProperty("ADMOB_BANNER_ID", "")}\""
        )

        // network_security_config에서 사용할 서버 호스트
        val serverHost = serverUrl
            .removePrefix("http://").removePrefix("https://")
            .removeSuffix("/").substringBefore(":")
        manifestPlaceholders["SERVER_HOST"] = serverHost
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Security
    implementation(libs.androidx.security.crypto)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    // Image Loading
    implementation(libs.coil.compose)

    // Google Mobile Ads
    implementation(libs.play.services.ads)

    // In-App Update
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
