plugins {
  kotlin("jvm")
  `java-library`
}

dependencies {
  implementation(libs.liquibase.core)
  implementation(libs.snakeyaml)
  testImplementation(Testing.kotest.runner.junit5)
}
