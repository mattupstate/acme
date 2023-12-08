import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate

plugins {
  id("acme.kotlin-library-conventions")
  alias(libs.plugins.nu.studer.jooq)
}

dependencies {
  jooqGenerator(project(":acme-lib:acme-lib-liquibase"))
  jooqGenerator(libs.org.testcontainers.postgresql)
  jooqGenerator(libs.org.postgresql)
  jooqGenerator(libs.ch.qos.logback.logback.classic)

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
        srcDir(tasks.withType(JooqGenerate::class))
      }
    }
  }
}

jooq {
  version.set("3.17.4")
  edition.set(JooqEdition.OSS)
  configurations {
    create("main") {
      jooqConfiguration.apply {
        logging = org.jooq.meta.jaxb.Logging.WARN
        jdbc.apply {
          driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
          url =
            "jdbc:tc:postgresql:11.5:///test?TC_INITFUNCTION=com.acme.liquibase.LiquibaseTestContainerInitializerKt::update"
        }
        generator.apply {
          name = "org.jooq.codegen.KotlinGenerator"
          database.apply {
            name = "org.jooq.meta.postgres.PostgresDatabase"
            includes = ".*"
            excludes = "databasechangelog.*|databasechangeloglock.*|pg_catalog.*|information_schema.*"
          }
          target.apply {
            packageName = "com.acme.sql"
          }
          strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
        }
      }
    }
  }
}

tasks {
  val liquibaseChangelogFile = "src/main/resources/db/changelog.yaml"

  withType(JooqGenerate::class) {
    inputs.dir(file(liquibaseChangelogFile).parent).withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
    outputs.cacheIf { true }
    javaExecSpec = Action {
      systemProperty("liquibase.changelogFile", liquibaseChangelogFile)
      systemProperty("logback.configurationFile", projectDir.resolve("logback-jooq.xml"))
    }
  }
}
