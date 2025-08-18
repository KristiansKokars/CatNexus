buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.2.0")
    }
}

plugins {
    id("com.android.application") version "8.12.0" apply false
    id("com.android.library") version "8.12.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("com.google.devtools.ksp") version "2.2.0-2.0.2" apply false
    id("io.sentry.android.gradle") version "5.8.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10" apply false
    id("androidx.room") version "2.7.2" apply false
}
