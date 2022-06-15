package com.acme.web.test.core

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

abstract class Page(protected val driver: WebDriver) {

  val root: WebElement by lazy {
    driver.findElement(rootLocator)
  }

  abstract val rootLocator: By
}
