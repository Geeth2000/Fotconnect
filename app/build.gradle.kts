plugins {
    alias(libs.plugins.android.application)
    // Ensure this line is present for Firebase services
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.fotconnect"
    compileSdk = 35 // Keep your compile SDK version updated

    defaultConfig {
        applicationId = "com.example.fotconnect"
        minSdk = 27 // Good minimum SDK for modern Android versions
        targetSdk = 35 // Target the latest stable Android version
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
    // Set Java compatibility to 11, which is commonly used now
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // AndroidX Libraries
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase
    implementation(libs.firebase.auth)          // Firebase Authentication via version catalog
    implementation(libs.firebase.database)      // Firebase Realtime Database

    // Google Play SafetyNet (for reCAPTCHA, older versions)
    implementation("com.google.android.gms:play-services-safetynet:18.1.0")

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.firebase:firebase-database:20.3.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2") // Use the latest version available
    implementation ("androidx.cardview:cardview:1.0.0") // If using CardView for article cards
    implementation ("com.google.firebase:firebase-storage")

}

