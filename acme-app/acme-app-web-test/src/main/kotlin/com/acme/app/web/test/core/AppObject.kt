package com.acme.app.web.test.core

import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.MalformedURLException
import java.net.URL
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

val logger = KotlinLogging.logger {}

open class AppObject(
  internal val root: URL,
  internal val urlMap: Map<KClass<out Any>, String>,
  internal val driver: WebDriver
) {
  constructor(
    url: String,
    urlMap: Map<KClass<out Any>, String>,
    driver: WebDriver
  ) : this(URL(url), urlMap, driver)

  val await get() = Await(driver)
}

fun <P : Page> AppObject.waitForPage(pageType: KClass<P>, timeout: Duration): P =
  pageType.primaryConstructor!!.call(driver).also {
    logger.debug("waiting for ${it.rootLocator}")
    WebDriverWait(driver, timeout.toJavaDuration()).until(
      ExpectedConditions.presenceOfElementLocated(it.rootLocator)
    )
  }

fun <P : Page> AppObject.navigate(pageType: KClass<P>, timeout: Duration = 5.seconds): P {
  val currentUrl: URL? = try {
    URL(driver.currentUrl)
  } catch (e: MalformedURLException) {
    null
  }

  val desiredUrl = URL(root, urlMap[pageType])

  if (currentUrl == null || currentUrl.isLogicallyDifferent(desiredUrl)) {
    driver.get(desiredUrl.toString())
  }

  return try {
    waitForPage(pageType, timeout)
  } catch (e: WebDriverException) {
    throw NavigationException("Wait condition for ${pageType.simpleName} failed", e)
  }
}

fun AppObject.waitFor(
  condition: ExpectedCondition<*>,
  duration: Duration = 5.seconds
) {
  WebDriverWait(driver, duration.toJavaDuration()).until(condition)
}

fun URL.isLogicallyDifferent(other: URL) =
  this.protocol != other.protocol || this.host != other.host || this.path != other.path

