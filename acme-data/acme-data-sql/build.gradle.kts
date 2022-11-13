import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqGenerate

plugins {
  kotlin("jvm")
  `java-library`
  id("nu.studer.jooq")
}

val jooqVersion = "3.16.4"

dependencies {
  jooqGenerator(project(":acme-lib:acme-lib-liquibase"))
  jooqGenerator(libs.org.testcontainers.postgresql)
  jooqGenerator(libs.org.postgresql.postgresql)
  jooqGenerator(libs.logback.classic)

  implementation(project(":acme-domain:acme-domain-core"))
  implementation(libs.kotlin.coroutines.jdbc)
  implementation(KotlinX.coroutines.core)
  api(libs.jooq)
  api(libs.jooq.kotlin)

  testImplementation(project(":acme-lib:acme-lib-liquibase"))
  testImplementation(Testing.kotest.runner.junit5)
  testImplementation(libs.org.postgresql.postgresql)
  testImplementation(libs.org.testcontainers.postgresql)
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
  version.set(jooqVersion)
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
    }
  }
}
