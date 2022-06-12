plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-web:acme-web-server"))
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
  implementation("com.appmattus.fixture:fixture:1.2.0")
  implementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
  implementation("com.google.truth:truth:1.0.1")
  implementation("com.google.truth.extensions:truth-java8-extension:1.0.1")
  implementation("info.picocli:picocli:4.6.3")
  implementation("io.cucumber:cucumber-java8:5.7.0")
  implementation("io.cucumber:cucumber-junit-platform-engine:5.7.0")
  implementation("io.cucumber:cucumber-picocontainer:5.7.0")
  implementation("io.github.bonigarcia:selenium-jupiter:4.1.0")
  implementation("io.ktor:ktor-client-apache:2.0.0")
  implementation("io.ktor:ktor-client-auth:2.0.0")
  implementation("io.ktor:ktor-client-logging:2.0.0")
  implementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
  implementation("org.junit.platform:junit-platform-launcher:1.8.2")
  implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.1.2")
  implementation("org.seleniumhq.selenium:selenium-java:4.1.2")
  implementation("org.testcontainers:selenium:1.17.2")
  runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

application {
  mainClass.set("com.acme.web.test.console.ConsoleLauncher")
  applicationDefaultJvmArgs = listOf(
    "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
    "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
    "-Dsel.jup.default.browser=chrome",
    "-Dsel.jup.browser.template.json.file=classpath:browsers-dev.json"
  )
}

jib {
  container {
    mainClass = application.mainClass.get()
  }
}
