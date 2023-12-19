import org.jooq.codegen.gradle.CodegenTask

plugins {
  id("acme.kotlin-library-conventions")
  alias(libs.plugins.org.jooq.codegen)
}

dependencies {
  jooqCodegen(files("src/main/resources"))
  jooqCodegen(project(":acme-lib:acme-lib-liquibase"))
  jooqCodegen(libs.org.testcontainers.postgresql)
  jooqCodegen(libs.org.postgresql)
  jooqCodegen(libs.ch.qos.logback.logback.classic)

  implementation(project(":acme-domain:acme-domain-core"))
  implementation(libs.com.michael.bull.kotlin.coroutines.jdbc)
  implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core)
  api(project(":acme-lib:acme-lib-jooq"))

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(libs.com.zaxxer.hikariCP)
  testImplementation(libs.io.kotest.kotest.runner.junit5)
  testImplementation(libs.org.postgresql)
  testImplementation(libs.org.postgresql.r2dbc)
  testImplementation(libs.org.slf4j.slf4j.simple)
  testImplementation(libs.org.testcontainers.postgresql)
  testImplementation(libs.org.testcontainers.r2dbc)
}

java {
  sourceSets {
    main {
      java {
        srcDir(tasks.withType(CodegenTask::class))
      }
    }
  }
}

jooq {
  configuration {
    logging = org.jooq.meta.jaxb.Logging.WARN
    jdbc {
      driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
      url =
        "jdbc:tc:postgresql:15.5:///test?TC_INITFUNCTION=com.acme.liquibase.LiquibaseTestContainerInitializerKt::update"
      user = "test"
      password = "test"
    }
    generator {
      name = "org.jooq.codegen.KotlinGenerator"
      database {
        name = "org.jooq.meta.postgres.PostgresDatabase"
        includes = ".*"
        excludes = "databasechangelog.*|databasechangeloglock.*|pg_catalog.*|information_schema.*"
      }
      target {
        packageName = "com.acme.sql"
        directory = projectDir.resolve("build/generated-src/jooq/main").toString()
      }
      generate {
        isKotlinNotNullPojoAttributes = true
        isKotlinNotNullRecordAttributes = true
        isKotlinNotNullInterfaceAttributes = true
      }
      strategy {
        name = "org.jooq.codegen.DefaultGeneratorStrategy"
      }
    }
  }
}

tasks {
  withType(CodegenTask::class) {
    inputs.dir(projectDir.resolve("src/main/resources/db"))
      .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs.cacheIf { true }
  }
}
