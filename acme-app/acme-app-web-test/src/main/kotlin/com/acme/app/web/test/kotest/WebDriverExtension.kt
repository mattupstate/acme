package com.acme.app.web.test.kotest

import io.github.bonigarcia.wdm.WebDriverManager
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Loosely inspired by io.kotest.extensions.testcontainers.JdbcTestContainerExtension
 * SEE: https://github.com/kotest/kotest-extensions-testcontainers/blob/master/src/main/kotlin/io/kotest/extensions/testcontainers/JdbcTestContainerExtension.kt
 */
class WebDriverExtension(browser: String) : MountableExtension<Nothing, WebDriver>, TestListener {

  constructor() : this(System.getProperty("webdriver.browser"))

  private val facade = WebDriverFacade(null)

  private val wdm: WebDriverManager = WebDriverManager.getInstance(browser)

  override fun mount(configure: Nothing.() -> Unit): WebDriver {
    wdm.setup()
    return facade
  }

  override suspend fun beforeTest(testCase: TestCase) {
    facade.setDriver(wdm.create())
  }

  override suspend fun afterTest(testCase: TestCase, result: TestResult) {
    facade.quit()
  }
}

class WebDriverFacade(private var driver: WebDriver? = null) : WebDriver {

  private fun getDriver(): WebDriver = driver ?: error("WebDriver not ready")

  internal fun setDriver(driver: WebDriver) {
    this.driver?.quit()
    this.driver = driver
  }

  override fun findElements(by: By?): MutableList<WebElement> =
    getDriver().findElements(by)

  override fun findElement(by: By?): WebElement =
    getDriver().findElement(by)

  override fun get(url: String?) = getDriver().get(url)

  override fun getCurrentUrl(): String = getDriver().currentUrl

  override fun getTitle(): String = getDriver().title

  override fun getPageSource(): String = getDriver().pageSource

  override fun close() = getDriver().close()

  override fun quit() = getDriver().quit()

  override fun getWindowHandles(): MutableSet<String> =
    getDriver().windowHandles

  override fun getWindowHandle(): String = getDriver().windowHandle

  override fun switchTo(): WebDriver.TargetLocator = getDriver().switchTo()

  override fun navigate(): WebDriver.Navigation = getDriver().navigate()

  override fun manage(): WebDriver.Options = getDriver().manage()
}
