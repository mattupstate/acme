plugins {
  id("acme.kotlin-application-conventions")
  kotlin("plugin.serialization")
}

dependencies {
  implementation("ch.qos.logback:logback-classic:1.2.3")
  implementation("com.appmattus.fixture:fixture-javafaker:1.2.0")
  implementation("com.appmattus.fixture:fixture:1.2.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.3")
  implementation("io.github.bonigarcia:webdrivermanager:5.2.1")
  implementation("io.kotest:kotest-runner-junit5:5.3.0")
  implementation("io.ktor:ktor-client-apache:2.0.0")
  implementation("io.ktor:ktor-client-auth:2.0.0")
  implementation("io.ktor:ktor-client-logging:2.0.0")
  implementation("org.fluentlenium:fluentlenium-kotest:5.0.4")
  implementation("org.junit.platform:junit-platform-launcher:1.8.2")
  implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.3.0")
}

application {
  mainClass.set("com.acme.app.web.test.console.ConsoleLauncher")
  applicationDefaultJvmArgs = listOf(
    "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
    "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
    "-Dfluentlenium.webDriver=remote",
    "-Dfluentlenium.capabilities=chrome-for-kubernetes",
    "-Dfluentlenium.remoteUrl=https://selenium-127-0-0-1.nip.io"
  )
}

jib {
  container {
    mainClass = application.mainClass.get()
  }
}

// tasks {
//   test {
//     jvmArgs = listOf(
//       "-Dacme.app.web.url=https://app-127-0-0-1.nip.io",
//       "-Dacme.app.mailhog.url=https://mailhog-127-0-0-1.nip.io",
//       "-Dfluentlenium.webDriver=remote",
//       "-Dfluentlenium.capabilities=\"chrome-for-kubernetes\"",
//       "-Dfluentlenium.remoteUrl=https://selenium-127-0-0-1.nip.io"
//     )
//   }
// }
