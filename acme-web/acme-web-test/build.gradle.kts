plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-web:acme-web-server"))
  // implementation(platform("org.junit:junit-bom:5.6.2"))
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
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
  implementation("org.junit.jupiter:junit-jupiter-api")
  implementation("org.junit.jupiter:junit-jupiter-params")
  implementation("org.junit.platform:junit-platform-console")
  implementation("org.junit.platform:junit-platform-launcher")
  implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.1.2")
  implementation("org.seleniumhq.selenium:selenium-java:4.1.2")
  implementation("org.testcontainers:selenium:1.17.2")
  runtimeOnly("org.junit.jupiter:junit-jupiter-engine")

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(project(":acme-lib:acme-lib-serialization"))
  // testImplementation(project(":acme-web:acme-web-client"))
  // testImplementation(project(":acme-web:acme-web-json"))
  testImplementation("com.github.java-json-tools:json-schema-validator:2.2.14")

  // testImplementation("io.kotest:kotest-runner-junit5:5.1.0")
  testImplementation("io.ktor:ktor-server-netty:2.0.0")
  testImplementation("io.kotest.extensions:kotest-assertions-ktor:1.0.3")
  testImplementation("io.kotest:kotest-assertions-json:5.3.0")
  testImplementation("org.testcontainers:postgresql:1.17.1")
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

// jib {
//   container {
//     mainClass = application.mainClass.get()
//   }
// }
