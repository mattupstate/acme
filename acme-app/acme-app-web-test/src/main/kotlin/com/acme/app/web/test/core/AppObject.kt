package com.acme.app.web.test.core

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

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
    WebDriverWait(driver, timeout.toJavaDuration()).until(
      ExpectedConditions.presenceOfElementLocated(it.rootLocator)
    )
  }

fun <P : Page> AppObject.expectPage(pageType: KClass<P>, timeout: Duration = 2.seconds): P =
  waitForPage(pageType, timeout)

fun <P : Page> AppObject.gotoPage(pageType: KClass<P>, timeout: Duration = 2.seconds): P {
  val desiredUrl = URL(root, urlMap[pageType])
  driver.get(desiredUrl.toString())
  return expectPage(pageType, timeout)
}
