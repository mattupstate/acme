package com.acme.web.test.core

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL
import java.time.Duration
import kotlin.reflect.KClass

open class AppObject(
  internal val root: URL,
  internal val urlMap: Map<KClass<out Any>, String>,
  internal val driver: WebDriver
) {
  constructor(
    root: String,
    urlMap: Map<KClass<out Any>, String>,
    driver: WebDriver
  ) : this(URL(root), urlMap, driver)
}

fun <P : Page> AppObject.waitForPage(pageType: KClass<P>, timeout: Duration): P =
  PageFactory.initElements(driver, pageType.java).also {
    WebDriverWait(driver, timeout).until(it.waitCondition)
  }

fun <P : Page> AppObject.navigate(pageType: KClass<P>, timeout: Duration = Duration.ofSeconds(5)): P =
  driver.get(URL(root, urlMap[pageType]).toString()).run {
    try {
      waitForPage(pageType, timeout)
    } catch (e: WebDriverException) {
      throw NavigationException("Wait condition for ${pageType.simpleName} failed", e)
    }
  }

fun AppObject.waitForInvisibilityOf(element: WebElement?, duration: Duration = Duration.ofSeconds(5)) {
  WebDriverWait(driver, duration).until(
    ExpectedConditions.invisibilityOf(element)
  )
}
