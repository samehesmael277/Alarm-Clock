// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        //maven {url = https://jitpack.io }
    }
    dependencies {
        val navVersion = "2.7.3"
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    kotlin("kapt") version "1.9.10"

    id ("com.google.dagger.hilt.android") version "2.48" apply false

    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}