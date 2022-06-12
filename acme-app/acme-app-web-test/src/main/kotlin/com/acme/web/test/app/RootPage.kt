package com.acme.web.test.app

import com.acme.web.test.core.Page
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class RootPage : Page {
  @FindBy(tagName = "app-root")
  var appRoot: WebElement? = null

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(appRoot)
}
