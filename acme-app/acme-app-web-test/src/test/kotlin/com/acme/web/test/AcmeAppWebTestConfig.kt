package com.acme.web.test

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.allure.AllureTestReporter

class AcmeAppWebTestConfig : AbstractProjectConfig() {

  override fun extensions(): List<Extension> =
    listOf(selenium, AllureTestReporter())

  companion object {
    val selenium = SeleniumExtension()
  }
}
