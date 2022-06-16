package com.acme.web.test.core

import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.MalformedURLException
import java.net.URL
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

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
}

fun <P : Page> AppObject.waitForPage(pageType: KClass<P>, timeout: Duration): P =
  pageType.primaryConstructor!!.call(driver).also {
    logger.debug("waiting for ${it.rootLocator}")
    WebDriverWait(driver, timeout).until(
      ExpectedConditions.presenceOfElementLocated(it.rootLocator)
    )
  }

fun <P : Page> AppObject.navigate(pageType: KClass<P>, timeout: Duration = Duration.ofSeconds(5)): P {
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
  duration: Duration = Duration.ofSeconds(5)
) {
  WebDriverWait(driver, duration).until(condition)
}

fun URL.isLogicallyDifferent(other: URL) =
  this.protocol != other.protocol || this.host != other.host || this.path != other.path

