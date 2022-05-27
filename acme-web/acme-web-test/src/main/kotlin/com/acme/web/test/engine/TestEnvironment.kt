package com.acme.web.test.engine

import mu.KotlinLogging
import java.io.IOException
import java.util.Properties

data class TestEnvironment(val packageName: String, val classNamePatterns: List<String>) {

  companion object {
    private val logger = KotlinLogging.logger {}

    private const val DEFAULT_CONFIG_FILE = "config.properties"
    private const val TESTS_PACKAGE_NAME_PROPERTY_NAME = "tests.packageName"
    private const val CLASS_NAME_PATTERNS_PROPERTY_NAME = "tests.classNamePatterns"
    private const val DEFAULT_PACKAGE_NAME_PROPERTY_VALUE = "com.acme.web.test"
    private const val DEFAULT_CLASS_NAME_PATTERNS_PROPERTY_VALUE = ".*Test,.*Tests,.*Spec"

    fun fromClassPathConfig(): TestEnvironment {
      val properties = Properties()
      val classLoader = EngineMain::class.java.classLoader
      try {
        classLoader.getResourceAsStream(DEFAULT_CONFIG_FILE)
          .use { config -> properties.load(config) }
      } catch (e: IOException) {
        logger.debug(
          "{} not found in classpath, using default properties",
          DEFAULT_CONFIG_FILE
        )
      } catch (e: NullPointerException) {
        logger.debug(
          "{} not found in classpath, using default properties",
          DEFAULT_CONFIG_FILE
        )
      }
      return TestEnvironment(
        properties.getProperty(
          TESTS_PACKAGE_NAME_PROPERTY_NAME,
          DEFAULT_PACKAGE_NAME_PROPERTY_VALUE
        ),
        properties.getProperty(
          CLASS_NAME_PATTERNS_PROPERTY_NAME,
          DEFAULT_CLASS_NAME_PATTERNS_PROPERTY_VALUE
        ).split(",".toRegex())
          .toList()
      )
    }
  }
}
