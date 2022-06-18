package com.acme.web.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.allure.AllureTestReporter

class AcmeWebTestProjectConfig : AbstractProjectConfig() {

  override fun extensions(): List<Extension> =
    listOf(apiServer, AllureTestReporter())

  companion object {
    val apiServer = MinimalAcmeWebServerExtension(
      serverHost = "127.0.0.1",
      serverPort = MinimalAcmeWebServerExtension.findOpenPort(),
    )
  }
}
