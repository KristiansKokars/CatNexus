buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.0")
    }
}

plugins {
    id("com.android.application") version "8.3.1" apply false
    id("com.android.library") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.devtools.ksp") version "2.0.0-1.0.22" apply false
    id("io.sentry.android.gradle") version "4.3.1" apply false
}
