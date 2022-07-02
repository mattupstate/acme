package com.acme.selenium

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

abstract class PageObject(protected val driver: WebDriver) {

  val root: WebElement by lazy {
    driver.findElement(rootLocator)
  }

  abstract val rootLocator: By
}
