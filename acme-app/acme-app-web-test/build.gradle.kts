plugins {
  application
  kotlin("jvm")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-selenium"))
  implementation(libs.logback.classic)
  implementation(libs.fixture.javafaker)
  implementation(libs.fixture)
  implementation(libs.selenium.jupiter)
  implementation(libs.kotlin.logging.jvm)
  implementation(libs.kotest.runner.junit5.jvm)
  implementation(libs.ktor.client.apache)
  implementation(libs.ktor.client.auth)
  implementation(libs.ktor.client.logging)
  implementation(libs.junit.jupiter.api)
  implementation(libs.junit.platform.launcher)
  runtimeOnly(libs.junit.jupiter.engine)
  runtimeOnly(libs.selenium.chrome.driver)
}

application {
  mainClass.set("com.acme.app.web.test.console.ConsoleLauncher")
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
