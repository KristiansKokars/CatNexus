buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.24")
    }
}

plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.android.library") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("io.sentry.android.gradle") version "4.3.1" apply false
}
