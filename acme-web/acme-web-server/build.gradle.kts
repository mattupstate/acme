plugins {
  id("acme.kotlin-conventions")
  application
  kotlin("plugin.serialization")
  alias(libs.plugins.org.hidetake.swagger.generator)
  alias(libs.plugins.com.google.cloud.tools.jib)
  alias(libs.plugins.com.ryandens.javaagent.jib)
}

val localRuntimeOnly: Configuration by configurations.creating

dependencies {
  swaggerUI(libs.org.webjars.swagger.ui)
  javaagent(libs.io.opentelemetry.javaagent.opentelemetry.javaagent)

  implementation(platform(libs.io.opentelemetry.opentelemetry.bom))
  implementation(project(":acme-data:acme-data-scheduling"))
  implementation(project(":acme-data:acme-data-sql"))
  implementation(project(":acme-domain:acme-domain-core"))
  implementation(project(":acme-domain:acme-domain-scheduling"))
  implementation(project(":acme-ktor:acme-ktor-i18n"))
  implementation(project(":acme-ktor:acme-ktor-logging"))
  implementation(project(":acme-ktor:acme-ktor-validation"))
  implementation(project(":acme-lib:acme-lib-serialization"))
  implementation(project(":acme-lib:acme-lib-validation"))
  implementation(libs.am.ik.timeflake.timeflake4j)
  implementation(libs.ch.qos.logback.logback.classic)
  implementation(libs.com.michael.bull.kotlin.coroutines.jdbc)
  implementation(libs.com.zaxxer.hikariCP)
  implementation(libs.io.github.microutils.kotlin.logging.jvm)
  implementation(libs.io.ktor.ktor.client.content.negotiation)
  implementation(libs.io.ktor.ktor.client.java)
  implementation(libs.io.ktor.ktor.serialization.kotlinx.json)
  implementation(libs.io.ktor.ktor.server.auth)
  implementation(libs.io.ktor.ktor.server.call.id)
  implementation(libs.io.ktor.ktor.server.call.logging)
  implementation(libs.io.ktor.ktor.server.content.negotiation)
  implementation(libs.io.ktor.ktor.server.default.headers)
  implementation(libs.io.ktor.ktor.server.forwarded.header)
  implementation(libs.io.ktor.ktor.server.locations)
  implementation(libs.io.ktor.ktor.server.metrics.micrometer)
  implementation(libs.io.ktor.ktor.server.netty)
  implementation(libs.io.ktor.ktor.server.status.pages)
  implementation(libs.io.micrometer.micrometer.registry.prometheus)
  implementation(libs.io.opentelemetry.opentelemetry.api)
  implementation(libs.io.opentelemetry.opentelemetry.sdk)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  implementation(libs.org.postgresql)
  implementation(libs.org.slf4j.slf4j.api)
  runtimeOnly(libs.org.glassfish.jakarta.el)

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.com.mattbertolini.liquibase.slf4j)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.testcontainers.postgresql)

  localRuntimeOnly(project(":acme-lib:acme-lib-liquibase"))
  localRuntimeOnly(libs.org.testcontainers.postgresql)
}

swaggerSources {
  register("acme") {
    setInputFile(file("spec.yaml"))
    ui.outputDir = file("${layout.buildDirectory}/swagger-ui-acme/swagger-ui")
  }
}

sourceSets.main {
  resources.srcDirs(
    swaggerSources.getByName("acme").ui.outputDir.parent,
  )
}

application {
  mainClass.set("io.ktor.server.netty.EngineMain")
}

jib {
  container {
    ports = listOf("8080")
    mainClass = application.mainClass.get()
    environment = mapOf(
      "OTEL_SERVICE_NAME" to "acme-web-server"
    )
  }

}

tasks {
  getByName<JavaExec>("run") {
    classpath += localRuntimeOnly
    systemProperty("logback.configurationFile", projectDir.resolve("logback-dev.xml"))
  }

  getByName("processResources") {
    dependsOn(generateSwaggerUI)
  }
}
