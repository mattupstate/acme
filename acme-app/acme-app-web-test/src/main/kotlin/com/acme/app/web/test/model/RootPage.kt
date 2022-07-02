package com.acme.app.web.test.model

import com.acme.selenium.PageObject
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class RootPage(driver: WebDriver) : PageObject(driver) {
  override val rootLocator: By = By.tagName("app-root-container")
}
