package com.acme.web.test

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.listeners.ProjectListener
import org.openqa.selenium.WebDriver

class SeleniumExtension : ProjectListener {

  var drivers: List<WebDriver> = emptyList()

  override suspend fun beforeProject() {
    WebDriverManager.chromedriver().setup()

    drivers = listOf(
      WebDriverManager.chromedriver().create()
    )
  }

  override suspend fun afterProject() {
    drivers.forEach {
      it.quit()
    }
  }
}
