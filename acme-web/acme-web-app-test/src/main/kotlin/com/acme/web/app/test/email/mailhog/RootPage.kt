package com.acme.web.app.test.email.mailhog

import com.acme.selenium.PageObject
import com.acme.selenium.wait
import io.ktor.util.reflect.typeInfo
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration.Companion.seconds

class RootPage(driver: WebDriver) : PageObject(driver) {

  override val rootLocator: By = By.className("messages")

  val navMenu: Navigation by lazy {
    Navigation(root.findElement(Navigation.LOCATOR))
  }

  private val messageList: MessageList by lazy {
    MessageList(root.findElement(MessageList.LOCATOR))
  }

  private val messagePreview: EmailPreview by lazy {
    EmailPreview(root.findElement(EmailPreview.LOCATOR))
  }

  fun emailDeliveredCondition(recipient: String, subject: String): ExpectedCondition<WebElement> =
    ExpectedConditions.presenceOfElementLocated(
      messageList.messageLocator(recipient, subject)
    )

  fun openEmail(recipient: String, subject: String) {
    driver.wait atMost 10.seconds until emailDeliveredCondition(recipient, subject)
    messageList.clickMessageFor(recipient, subject)
    driver.wait atMost 2.seconds until ExpectedConditions.and(
      ExpectedConditions.presenceOfElementLocated(EmailPreview.LOCATOR),
      ExpectedConditions.presenceOfElementLocated(By.id("preview-html"))
    )
  }

  inline fun <reified T : Any> withEmailMessageContent(block: T.() -> Unit) {
    val emailTypeInfo = typeInfo<T>()
    webDriver.switchTo().frame("preview-html")
    (emailTypeInfo.type.primaryConstructor!!.call(webDriver.findElement(By.tagName("body"))) as T).apply(block)
    webDriver.switchTo().defaultContent()
  }

  class MessageList(private val root: WebElement) {
    companion object {
      val LOCATOR: By = By.xpath("//div[contains(@class, 'messages')]")
    }

    fun messageLocator(recipient: String, subject: String): By =
      By.xpath(
        """
      //div[
        contains(@class, 'msglist-message') and
        contains(.//div, '$recipient') and
        contains(.//span, '$subject')
      ]
    """.trimIndent()
      )

    fun clickMessageFor(recipient: String, subject: String) =
      root.findElement(messageLocator(recipient, subject)).click()
  }

  class Navigation(private val root: WebElement) {
    companion object {
      val LOCATOR: By = By.xpath("//ul[contains(@class, 'nav-pills')]")
    }

    val inboxButton: WebElement by lazy {
      root.findElement(By.partialLinkText("Inbox"))
    }
  }

  class EmailPreview(private val root: WebElement) {
    companion object {
      val LOCATOR: By = By.xpath("//div[contains(@class, 'preview')]")
    }
  }

  @PublishedApi
  internal val webDriver: WebDriver
    get() = driver
}
