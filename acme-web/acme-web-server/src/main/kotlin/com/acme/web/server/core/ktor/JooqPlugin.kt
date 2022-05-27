package com.acme.web.server.core.ktor

import io.ktor.server.application.createApplicationPlugin
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class JooqPluginConfiguration {
  var threadContextName: String = "JooqTransactionContext"
}

val JooqPlugin = createApplicationPlugin(
  name = "JooqPlugin",
  createConfiguration = ::JooqPluginConfiguration
) {
  val databaseContext = newSingleThreadContext(pluginConfig.threadContextName)

  onCall {
    withContext(databaseContext) {

    }
  }
}
