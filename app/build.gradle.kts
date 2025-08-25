plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    id("io.sentry.android.gradle")
    id("org.jetbrains.kotlin.plugin.compose")
    id("androidx.room")
}

android {
    namespace = "com.kristianskokars.catnexus"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kristianskokars.catnexus"
        minSdk = 23
        targetSdk = 36
        versionCode = 18
        versionName = "1.5.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //         testInstrumentationRunner = "com.kristianskokars.catnexus.app.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")

            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Android
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.compose.ui:ui:1.9.0")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.core:core-splashscreen:1.2.0-rc01")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.1.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Room
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    implementation("androidx.test:runner:1.7.0")
    ksp("androidx.room:room-compiler:2.7.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.57")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    ksp("com.google.dagger:hilt-android-compiler:2.57")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.3")

    // Coil
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("io.coil-kt:coil-gif:2.7.0")

    // Compose Destinations
    implementation("io.github.raamcosta.compose-destinations:core:2.2.0")
    ksp("io.github.raamcosta.compose-destinations:ksp:2.2.0")

    // Timber
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Haze
    implementation("dev.chrisbanes.haze:haze:1.6.9")

    // Zoomable
    implementation("net.engawapg.lib:zoomable:2.8.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:5.1.0")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")

    debugImplementation("androidx.compose.ui:ui-tooling:1.9.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.9.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.9.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57")
    kspAndroidTest("androidx.hilt:hilt-compiler:1.2.0")
    androidTestImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

sentry {
    org.set("kristians-kokars")
    projectName.set("cat-nexus")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}

room {
    schemaDirectory("$projectDir/schemas")
}
