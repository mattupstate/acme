package com.acme.web.test.core

import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition

interface Page {
  val waitCondition: ExpectedCondition<WebElement>
}
