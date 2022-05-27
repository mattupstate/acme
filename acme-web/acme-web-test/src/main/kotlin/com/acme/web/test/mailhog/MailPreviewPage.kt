package com.acme.web.test.mailhog

import com.acme.web.test.core.Page
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class MailPreviewPage : Page {
  @FindBy(xpath = "//div[contains(@class, \"preview\"")
  private val rootWebElement: WebElement? = null

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)
}
