package com.acme.web.server.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class AcmeWebTestProjectConfig : AbstractProjectConfig() {

  override fun extensions(): List<Extension> =
    listOf(apiServer)

  companion object {
    val apiServer = MinimalAcmeWebServerExtension(
      serverHost = "127.0.0.1",
      serverPort = MinimalAcmeWebServerExtension.findOpenPort(),
    )
  }
}
