plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
  id("io.qameta.allure-report") version "2.10.0"
}

dependencies {
  runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
  implementation("com.appmattus.fixture:fixture:1.2.0")
  implementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
  implementation("io.ktor:ktor-client-apache:2.0.0")
  implementation("io.ktor:ktor-client-auth:2.0.0")
  implementation("io.ktor:ktor-client-logging:2.0.0")
  implementation("org.seleniumhq.selenium:selenium-java:4.2.2")
  // implementation("io.kotest:kotest-runner-junit5:5.3.0")
  implementation("org.junit.platform:junit-platform-launcher:1.8.2")

  testImplementation("io.github.bonigarcia:webdrivermanager:5.2.0")
  testImplementation("io.kotest.extensions:kotest-extensions-allure:1.2.0")
}

allure {
  version.set("2.18.1")
}

application {
  mainClass.set("com.acme.web.test.console.ConsoleLauncher")
  applicationDefaultJvmArgs = listOf(
    "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
    "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
    // "-Dsel.jup.default.browser=chrome",
    // "-Dsel.jup.browser.template.json.file=classpath:browsers-dev.json"
  )
}

jib {
  container {
    mainClass = application.mainClass.get()
  }
}

tasks {
  test {
    jvmArgs = listOf(
      "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
      "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
    )
  }
}
