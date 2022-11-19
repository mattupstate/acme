plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  dependencies {
    implementation(libs.org.jetbrains.kotlin.jvm.gradle.plugin)
    implementation(libs.org.jetbrains.kotlin.serialization.gradle.plugin)
    implementation(libs.org.jlleitschuh.ktlint.gradle.plugin)
    implementation(libs.com.diffplug.spotless.gradle.plugin)
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
