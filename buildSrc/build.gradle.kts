plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  dependencies {
    implementation("gradle.plugin.com.google.cloud.tools:jib-gradle-plugin:3.2.1")
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.6.21")
    implementation("org.jetbrains.kotlin.plugin.serialization:org.jetbrains.kotlin.plugin.serialization.gradle.plugin:1.6.21")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.5.1")
  }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "11"
    }
  }
}
