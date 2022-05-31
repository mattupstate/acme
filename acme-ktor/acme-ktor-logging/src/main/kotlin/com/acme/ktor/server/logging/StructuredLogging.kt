package com.acme.ktor.server.logging

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.log
import io.ktor.util.AttributeKey

// SEE: https://github.com/ktorio/ktor-samples/blob/main/generic/samples/structured-logging/

private val structuredLoggerKey = AttributeKey<StructuredLogger>("structuredLoggerKey")

val ApplicationCall.logger get() = attributes[structuredLoggerKey]

class StructuredLoggingConfiguration {
  internal var attachBlock: ApplicationCall.() -> Map<String, String> = {
    mapOf("empty" to "thing")
  }
  fun attach(block: ApplicationCall.() -> Map<String, String>) {
    attachBlock = block
  }
}

val StructuredLogging = createApplicationPlugin(
  name = "StructureLogging",
  createConfiguration = ::StructuredLoggingConfiguration
) {
  val attachBlock = pluginConfig.attachBlock

  onCall {
    it.attributes.put(structuredLoggerKey, StructuredLogger(it.application.log, it.attachBlock()))
  }
}
