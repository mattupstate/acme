plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-lib:acme-lib-selenium"))
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
  implementation("com.appmattus.fixture:fixture:1.2.0")
  implementation("io.github.bonigarcia:selenium-jupiter:4.2.0")
  implementation("io.kotest:kotest-runner-junit5-jvm:5.3.2")
  implementation("io.ktor:ktor-client-apache:2.0.0")
  implementation("io.ktor:ktor-client-auth:2.0.0")
  implementation("io.ktor:ktor-client-logging:2.0.0")
  implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  implementation("org.junit.platform:junit-platform-launcher:1.8.2")
  runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
  runtimeOnly("org.seleniumhq.selenium:selenium-chrome-driver:4.3.0")
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

jib {
  container {
    mainClass = application.mainClass.get()
  }
}
