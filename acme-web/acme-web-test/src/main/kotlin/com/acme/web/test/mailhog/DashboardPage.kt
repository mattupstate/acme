package com.acme.web.test.mailhog

import com.acme.web.test.core.Page
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class DashboardPage : Page {
  @FindBy(className = "messages")
  private val rootWebElement: WebElement? = null

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)

  fun clickMessageListItemContaining(recipient: String, subject: String) {
    rootWebElement!!.findElement(
      By.className("msglist-message")
    ).findElement(By.xpath(".//div[contains(.,\"$recipient\") and contains(.,\"$subject\"]")).click()
  }
}
