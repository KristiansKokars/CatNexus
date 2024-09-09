buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.10")
    }
}

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.10" apply false
    id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false
    id("io.sentry.android.gradle") version "4.3.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.10" apply false
}
