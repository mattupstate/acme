plugins {
  id("acme.kotlin-conventions")
  application
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-selenium"))
  implementation(kotlin("reflect"))
  implementation(libs.net.datafaker.datafaker)
  implementation(libs.io.github.bonigarcia.selenium.jupiter)
  implementation(libs.io.github.oshai.kotlin.logging.jvm)
  implementation(libs.io.kotest.kotest.runner.junit5.jvm)
  implementation(libs.io.ktor.ktor.client.apache)
  implementation(libs.io.ktor.ktor.client.auth)
  implementation(libs.io.ktor.ktor.client.logging)
  implementation(libs.net.logstash.logback.logstash.logback.encoder)
  implementation(libs.org.junit.jupiter.junit.jupiter.api)
  implementation(libs.org.junit.platform.junit.platform.launcher)
  implementation(libs.org.slf4j.slf4j.api)
  runtimeOnly(libs.org.junit.jupiter.junit.jupiter.engine)
  runtimeOnly(libs.org.seleniumhq.selenium.selenium.chrome.driver)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
}

application {
  mainClass.set("com.acme.web.app.test.console.ConsoleLauncher")
  applicationDefaultJvmArgs = listOf(
    "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
    "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
    "-Djunit.platform.output.capture.stderr=true",
    "-Dwebdriver.browser=chrome",
  )
}

// jib {
//   container {
//     mainClass = application.mainClass.get()
//   }
// }
