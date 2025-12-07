import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
}

android {
    namespace = "com.example.aichathelp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.aichathelp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        val majorVersion = 1
        val minorVersion = 0
        val patchVersion = 0

        versionCode = majorVersion * 10000 + minorVersion * 100 + patchVersion
        versionName = "$majorVersion.$minorVersion.$patchVersion"
        base.archivesName = "AiChatHelp-$versionName"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val apiKey = System.getenv("API_KEY")
            ?: Properties().apply {
                load(rootProject.file("local.properties").inputStream())
            }.getProperty("API_KEY")
            ?: throw GradleException("API_KEY is missing!")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.hilt.android)
    implementation(libs.hilt.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.retrofit)

    implementation(libs.commonmark)

    implementation(libs.coil)
}