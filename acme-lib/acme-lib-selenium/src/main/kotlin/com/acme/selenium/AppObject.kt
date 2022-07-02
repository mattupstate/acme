package com.acme.selenium

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

abstract class AppObject(internal val driver: WebDriver) {
  abstract val pages: Map<KClass<out Any>, String>
  abstract val rootUrl: URL

  private fun <P : PageObject> waitForPage(pageType: KClass<P>, timeout: Duration): P =
    pageType.primaryConstructor!!.call(driver).also {
      WebDriverWait(driver, timeout.toJavaDuration()).until(
        ExpectedConditions.presenceOfElementLocated(it.rootLocator)
      )
    }

  fun <P : PageObject> expectPage(pageType: KClass<P>, timeout: Duration = 2.seconds): P =
    waitForPage(pageType, timeout)

  fun <P : PageObject> gotoPage(pageType: KClass<P>, timeout: Duration = 2.seconds): P {
    val desiredUrl = URL(rootUrl, pages[pageType])
    driver.get(desiredUrl.toString())
    return expectPage(pageType, timeout)
  }
}
