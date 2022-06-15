package com.acme.web.test.app

import com.acme.web.test.core.Page
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class RootPage(driver: WebDriver) : Page(driver) {
  override val rootLocator: By = By.name("app-root")
}
