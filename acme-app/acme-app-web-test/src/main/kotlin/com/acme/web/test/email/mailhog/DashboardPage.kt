package com.acme.web.test.email.mailhog

import com.acme.web.test.core.Page
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions

class DashboardPage : Page {
  @FindBy(className = "messages")
  private val rootWebElement: WebElement? = null

  private fun findElementFromRoot(by: By) =
    rootWebElement!!.findElement(by)

  override val waitCondition: ExpectedCondition<WebElement>
    get() = ExpectedConditions.visibilityOf(rootWebElement)

  fun navigateToMessageList() {
    findElementFromRoot(
      By.xpath("/html/body/div/div/div[1]/ul/li[2]/a")
    ).click()
  }

  fun clickMessageListItemContaining(recipient: String) {
    findElementFromRoot(
      By.className("msglist-message")
    ).findElement(
      By.xpath(".//div[contains(.,\"$recipient\")]")
    ).click()
  }
}
