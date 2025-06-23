plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    id("kotlin-kapt") // Required for Room annotation processing
}

android {
    namespace = "com.example.myapplicationtestsade"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplicationtestsade"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

    // ******************** CORE DEPENDENCIES ********************
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ******************** FEATURE DEPENDENCIES ********************
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // QR Code Generation
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Camera
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // AR Core
    implementation("com.google.ar:core:1.40.0")

    // ******************** ROOM DATABASE DEPENDENCIES ********************
    // Room runtime
    implementation("androidx.room:room-runtime:2.6.1")

    // Room Kotlin Extensions and Coroutines support
    implementation("androidx.room:room-ktx:2.6.1")

    // Room annotation processor
    kapt("androidx.room:room-compiler:2.6.1")

    // ********************COROUTINES SUPPORT ********************
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ******************** COMPOSE STATE INTEGRATION ********************
    implementation("androidx.compose.runtime:runtime-livedata:1.5.8")

    // Google ML Kit for Barcode/QR Code scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX for image analysis
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-beta01")

    // Better QR code detection
    implementation("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")

}

// KAPT configuration AFTER dependencies block
kapt {
    correctErrorTypes = true
}
